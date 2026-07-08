package com.novahire.service;

import com.novahire.ai.AIEvaluationService;
import com.novahire.ai.AIProperties;
import com.novahire.ai.EvaluationResult;
import com.novahire.dto.response.EvaluationResponse;
import com.novahire.dto.response.InterviewResponse;
import com.novahire.dto.response.ReportResponse;
import com.novahire.entity.Interview;
import com.novahire.entity.InterviewAnswer;
import com.novahire.entity.InterviewEvaluation;
import com.novahire.entity.InterviewQuestion;
import com.novahire.entity.QuestionEvaluation;
import com.novahire.entity.User;
import com.novahire.exception.BadRequestException;
import com.novahire.exception.ForbiddenException;
import com.novahire.exception.ResourceNotFoundException;
import com.novahire.repository.InterviewAnswerRepository;
import com.novahire.repository.InterviewEvaluationRepository;
import com.novahire.repository.InterviewQuestionRepository;
import com.novahire.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Owns the Sprint 5 evaluation lifecycle: trigger (idempotent, with retry), read stored evaluation,
 * and build the professional report. Kept separate from {@link InterviewSessionService} the same way
 * that service is kept separate from {@link InterviewService} — each owns one slice of the aggregate.
 *
 * <p>Delegates the actual AI call to {@link AIEvaluationService} (provider-agnostic) and never
 * touches {@link InterviewAnswer} rows — answers are read-only here, so they can never be lost.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final InterviewEvaluationRepository evaluationRepository;
    private final AIEvaluationService aiEvaluationService;
    private final AIProperties aiProperties;

    /**
     * Runs evaluation once and idempotently.
     *
     * <p>Concurrency: takes the same pessimistic row lock as session start
     * ({@link InterviewRepository#findByIdForUpdate}) so a double-click or StrictMode double-invoke
     * can't launch two AI evaluations for the same interview — the second caller blocks, then sees
     * the now-COMPLETED result and returns it without calling the provider again.
     *
     * <p>Idempotency: a COMPLETED evaluation is returned as-is; the AI provider is only called when
     * the evaluation is absent, PENDING, or FAILED (manual retry).
     */
    @Transactional
    public EvaluationResponse evaluate(User user, Long interviewId) {
        Interview interview = interviewRepository.findByIdForUpdate(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));
        assertOwner(user, interview);

        if (interview.getStatus() != Interview.Status.COMPLETED) {
            throw new BadRequestException("Only a completed interview can be evaluated");
        }

        Optional<InterviewEvaluation> existing = evaluationRepository.findByInterviewId(interviewId);
        if (existing.isPresent() && existing.get().getStatus() == InterviewEvaluation.Status.COMPLETED) {
            log.info("Evaluation already completed for interview id={}; returning stored result (no AI call)",
                    interviewId);
            return buildResponse(existing.get(), interviewId);
        }

        List<InterviewQuestion> questions =
                questionRepository.findByInterviewIdOrderByOrderIndexAsc(interviewId);
        if (questions.isEmpty()) {
            throw new BadRequestException("This interview has no questions to evaluate");
        }
        Map<Long, String> answerTextByQuestionId = loadAnswerText(interviewId);

        // Create-or-reuse the evaluation row, mark IN_PROGRESS, and flush so a concurrent (post-lock)
        // reader sees the in-flight state rather than an absent row.
        InterviewEvaluation evaluation = existing.orElseGet(() -> InterviewEvaluation.builder()
                .interview(interview)
                .status(InterviewEvaluation.Status.PENDING)
                .attemptCount(0)
                .build());
        evaluation.setStatus(InterviewEvaluation.Status.IN_PROGRESS);
        evaluation.setAttemptCount((evaluation.getAttemptCount() == null ? 0 : evaluation.getAttemptCount()) + 1);
        evaluation = evaluationRepository.saveAndFlush(evaluation);

        if (!aiProperties.isProviderConfigured()) {
            return failEvaluation(evaluation, interviewId,
                    "AI provider is not configured; evaluation is unavailable. Set GEMINI_API_KEY and retry.");
        }

        EvaluationResult result;
        try {
            result = runWithRetry(interview, questions, answerTextByQuestionId, interviewId);
        } catch (Exception e) {
            return failEvaluation(evaluation, interviewId, "AI evaluation failed after retry: " + e.getMessage());
        }

        applyResult(evaluation, questions, result);
        evaluation.setStatus(InterviewEvaluation.Status.COMPLETED);
        evaluation.setEvaluatedAt(LocalDateTime.now());
        evaluation.setErrorMessage(null);
        evaluation.setProvider(aiProperties.getProvider());
        evaluation.setModel(aiProperties.getGemini().getModel());
        evaluation.setPromptVersion(aiProperties.getPrompt().getVersion());
        evaluation.setEvaluationVersion(aiProperties.getEvaluation().getVersion());
        evaluationRepository.save(evaluation);

        // Backward-compatible sync: light up the Sprint 2 dashboard stats (avgScore/bestScore read
        // Interview.score) and the existing overallFeedback field — no schema change, purely additive.
        interview.setScore(result.overallScore());
        interview.setOverallFeedback(result.summary());
        interviewRepository.save(interview);

        log.info("Evaluation completed for interview id={}: overall={}, recommendation={}",
                interviewId, result.overallScore(), evaluation.getRecommendation());
        return buildResponse(evaluation, interviewId);
    }

    /** Returns the stored evaluation. 404 if the interview was never evaluated. */
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluation(User user, Long interviewId) {
        Interview interview = getOwnedInterview(user, interviewId);
        InterviewEvaluation evaluation = evaluationRepository.findByInterviewId(interview.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation for interview", interviewId));
        return buildResponse(evaluation, interviewId);
    }

    /** Returns the professional report (interview config + evaluation). 404 if never evaluated. */
    @Transactional(readOnly = true)
    public ReportResponse getReport(User user, Long interviewId) {
        Interview interview = getOwnedInterview(user, interviewId);
        InterviewEvaluation evaluation = evaluationRepository.findByInterviewId(interview.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation for interview", interviewId));
        return ReportResponse.builder()
                .interview(InterviewResponse.fromInterview(interview))
                .evaluation(buildResponse(evaluation, interviewId))
                .build();
    }

    // ── Internals ──────────────────────────────────────────────────────────────

    /** One AI attempt, then exactly one retry, per the failure-handling spec. */
    private EvaluationResult runWithRetry(Interview interview,
                                          List<InterviewQuestion> questions,
                                          Map<Long, String> answerTextByQuestionId,
                                          Long interviewId) {
        try {
            return aiEvaluationService.evaluate(interview, questions, answerTextByQuestionId);
        } catch (Exception first) {
            log.warn("AI evaluation attempt 1 failed for interview id={}: {} — retrying once",
                    interviewId, first.getMessage());
            try {
                return aiEvaluationService.evaluate(interview, questions, answerTextByQuestionId);
            } catch (Exception second) {
                log.error("AI evaluation retry failed for interview id={}: {}", interviewId, second.getMessage());
                throw second;
            }
        }
    }

    /**
     * Marks the evaluation FAILED and returns it. Runs inside the caller's transaction and swallows
     * the AI error (no rollback) so the FAILED status + errorMessage persist for a later manual retry.
     * Candidate answers are never touched.
     */
    private EvaluationResponse failEvaluation(InterviewEvaluation evaluation, Long interviewId, String message) {
        log.error("Evaluation FAILED for interview id={}: {}", interviewId, message);
        evaluation.setStatus(InterviewEvaluation.Status.FAILED);
        evaluation.setErrorMessage(message);
        evaluationRepository.save(evaluation);
        return buildResponse(evaluation, interviewId);
    }

    private void applyResult(InterviewEvaluation evaluation,
                             List<InterviewQuestion> questions,
                             EvaluationResult result) {
        evaluation.setOverallScore(result.overallScore());
        evaluation.setTechnicalScore(result.technicalScore());
        evaluation.setCommunicationScore(result.communicationScore());
        evaluation.setProblemSolvingScore(result.problemSolvingScore());
        evaluation.setConfidenceScore(result.confidenceScore());
        evaluation.setRecommendation(InterviewEvaluation.Recommendation.fromString(result.recommendation()));
        evaluation.setStrengths(new ArrayList<>(result.strengths()));
        evaluation.setWeaknesses(new ArrayList<>(result.weaknesses()));
        evaluation.setImprovementRoadmap(new ArrayList<>(result.improvementRoadmap()));
        evaluation.setSummary(result.summary());

        Map<Integer, InterviewQuestion> byOrder = questions.stream()
                .collect(Collectors.toMap(InterviewQuestion::getOrderIndex, Function.identity(), (a, b) -> a));

        List<QuestionEvaluation> verdicts = new ArrayList<>();
        for (EvaluationResult.QuestionResult qr : result.questions()) {
            InterviewQuestion q = byOrder.get(qr.orderIndex());
            if (q == null) {
                log.debug("AI returned a verdict for unknown orderIndex={}, ignoring", qr.orderIndex());
                continue;
            }
            verdicts.add(QuestionEvaluation.builder()
                    .question(q)
                    .score(qr.score())
                    .strengths(qr.strengths())
                    .weaknesses(qr.weaknesses())
                    .improvementSuggestions(qr.improvementSuggestions())
                    .explanation(qr.explanation())
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        evaluation.replaceQuestionEvaluations(verdicts);
    }

    private EvaluationResponse buildResponse(InterviewEvaluation evaluation, Long interviewId) {
        return EvaluationResponse.fromEntity(evaluation, loadAnswerText(interviewId));
    }

    private Map<Long, String> loadAnswerText(Long interviewId) {
        return answerRepository.findByInterviewId(interviewId).stream()
                .filter(a -> a.getAnswerText() != null)
                .collect(Collectors.toMap(
                        a -> a.getQuestion().getId(),
                        InterviewAnswer::getAnswerText,
                        (a, b) -> a));
    }

    private Interview getOwnedInterview(User user, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));
        assertOwner(user, interview);
        return interview;
    }

    private void assertOwner(User user, Interview interview) {
        if (!interview.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this interview");
        }
    }
}
