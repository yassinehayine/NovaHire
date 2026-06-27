package com.novahire.service;

import com.novahire.dto.request.SaveAnswerRequest;
import com.novahire.dto.response.InterviewAnswerResponse;
import com.novahire.dto.response.InterviewQuestionResponse;
import com.novahire.dto.response.InterviewResponse;
import com.novahire.dto.response.InterviewSessionResponse;
import com.novahire.entity.Interview;
import com.novahire.entity.InterviewAnswer;
import com.novahire.entity.InterviewQuestion;
import com.novahire.entity.User;
import com.novahire.exception.BadRequestException;
import com.novahire.exception.ForbiddenException;
import com.novahire.exception.ResourceNotFoundException;
import com.novahire.repository.InterviewAnswerRepository;
import com.novahire.repository.InterviewQuestionRepository;
import com.novahire.repository.InterviewRepository;
import com.novahire.service.question.QuestionGenerationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Owns the runtime/session lifecycle of an interview (start, answer, finish) — kept separate
 * from {@link InterviewService}, which only handles CRUD/stats over the Interview aggregate
 * and has no reason to depend on question/answer repositories or the generation strategy.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewSessionService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAnswerRepository answerRepository;
    private final QuestionGenerationStrategy questionGenerationStrategy;

    /**
     * Loads the session, lazily starting it (generating questions, setting startedAt,
     * flipping CREATED -> IN_PROGRESS) the first time it's accessed. Reloading an already
     * started session never regenerates questions or resets startedAt.
     *
     * <p>Uses a row lock (not the plain ownership-check read) because this method does a
     * check-then-act on {@code status}: two near-simultaneous calls for the same interview
     * (e.g. React 18 StrictMode double-invoking the loading effect) would otherwise both
     * observe CREATED and each generate their own question set. The lock serializes them —
     * the second caller blocks until the first commits, then re-reads IN_PROGRESS and skips
     * generation.
     */
    @Transactional
    public InterviewSessionResponse loadSession(User user, Long interviewId) {
        Interview interview = interviewRepository.findByIdForUpdate(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));
        if (!interview.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this interview");
        }

        if (interview.getStatus() == Interview.Status.CREATED) {
            List<InterviewQuestion> questions = questionGenerationStrategy.generateQuestions(interview);
            questionRepository.saveAll(questions);
            interview.setStartedAt(LocalDateTime.now());
            interview.setStatus(Interview.Status.IN_PROGRESS);
            interview = interviewRepository.save(interview);
            log.info("Interview session started id={} for user id={}", interview.getId(), user.getId());
        }

        List<InterviewQuestion> questions = questionRepository.findByInterviewIdOrderByOrderIndexAsc(interviewId);
        List<InterviewAnswer> answers = answerRepository.findByInterviewId(interviewId);

        return InterviewSessionResponse.builder()
                .interview(InterviewResponse.fromInterview(interview))
                .questions(questions.stream().map(InterviewQuestionResponse::fromQuestion).toList())
                .answers(answers.stream().map(InterviewAnswerResponse::fromAnswer).toList())
                .startedAt(interview.getStartedAt())
                .durationMinutes(interview.getDurationMinutes())
                .build();
    }

    /** Upserts the answer for one question — one row per question, enforced at the DB level. */
    @Transactional
    public InterviewAnswerResponse saveAnswer(User user, Long interviewId, Long questionId, SaveAnswerRequest request) {
        Interview interview = getOwnedInterview(user, interviewId);

        if (interview.getStatus() != Interview.Status.IN_PROGRESS) {
            throw new BadRequestException("This interview is not currently in progress");
        }
        if (isExpired(interview)) {
            throw new BadRequestException("Time is up for this interview");
        }

        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        if (!question.getInterview().getId().equals(interviewId)) {
            throw new ForbiddenException("This question does not belong to this interview");
        }

        InterviewAnswer answer = answerRepository.findByQuestionId(questionId)
                .orElseGet(() -> InterviewAnswer.builder()
                        .question(question)
                        .interview(interview)
                        .createdAt(LocalDateTime.now())
                        .build());

        answer.setAnswerText(request.getAnswerText());
        answer.setTimeSpentSeconds(request.getTimeSpentSeconds());
        answer.setAnsweredAt(LocalDateTime.now());

        InterviewAnswer saved = answerRepository.save(answer);
        return InterviewAnswerResponse.fromAnswer(saved);
    }

    /**
     * Idempotent: finishing an already-COMPLETED session just returns its current state
     * instead of erroring, so a double-click or flaky-network retry is harmless.
     * Stays allowed past time-expiry — a late submit shouldn't be blocked, only saveAnswer is.
     */
    @Transactional
    public InterviewResponse finishSession(User user, Long interviewId) {
        Interview interview = getOwnedInterview(user, interviewId);

        if (interview.getStatus() == Interview.Status.COMPLETED) {
            return InterviewResponse.fromInterview(interview);
        }
        if (interview.getStatus() != Interview.Status.IN_PROGRESS) {
            throw new BadRequestException("This interview cannot be finished from its current state");
        }

        interview.setStatus(Interview.Status.COMPLETED);
        interview.setCompletedAt(LocalDateTime.now());
        Interview saved = interviewRepository.save(interview);
        log.info("Interview finished id={} for user id={}", saved.getId(), user.getId());
        return InterviewResponse.fromInterview(saved);
    }

    private Interview getOwnedInterview(User user, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));
        if (!interview.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this interview");
        }
        return interview;
    }

    private boolean isExpired(Interview interview) {
        if (interview.getStartedAt() == null) {
            return false;
        }
        LocalDateTime deadline = interview.getStartedAt().plusMinutes(interview.getDurationMinutes());
        return LocalDateTime.now().isAfter(deadline);
    }
}
