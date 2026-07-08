package com.novahire.dto.response;

import com.novahire.entity.InterviewEvaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * The stored, normalized interview evaluation as exposed to the frontend. Carries the report-level
 * scores plus the per-question verdicts. Reflects {@code status}, so a PENDING/IN_PROGRESS/FAILED
 * evaluation returns with empty scores and (for FAILED) an {@code errorMessage} the UI can act on.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {

    private Long interviewId;
    /** PENDING | IN_PROGRESS | COMPLETED | FAILED */
    private String status;

    // Report scores (0–100; null unless COMPLETED)
    private Integer overallScore;
    private Integer technicalScore;
    private Integer communicationScore;
    private Integer problemSolvingScore;
    private Integer confidenceScore;

    /** STRONG_HIRE | HIRE | BORDERLINE | NO_HIRE */
    private String recommendation;
    /** Human-readable label, e.g. "Strong Hire". */
    private String recommendationLabel;

    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> improvementRoadmap;
    private String summary;

    private Integer attemptCount;
    /** Populated only when status = FAILED. */
    private String errorMessage;
    private LocalDateTime evaluatedAt;

    private List<QuestionEvaluationResponse> questionEvaluations;

    /**
     * @param evaluation             the stored evaluation (any status)
     * @param answerTextByQuestionId candidate answer text keyed by question id, for the review UI
     */
    public static EvaluationResponse fromEntity(InterviewEvaluation evaluation,
                                                Map<Long, String> answerTextByQuestionId) {
        InterviewEvaluation.Recommendation rec = evaluation.getRecommendation();
        List<QuestionEvaluationResponse> questions = evaluation.getQuestionEvaluations().stream()
                .map(qe -> QuestionEvaluationResponse.from(
                        qe, answerTextByQuestionId.get(qe.getQuestion().getId())))
                .toList();

        return EvaluationResponse.builder()
                .interviewId(evaluation.getInterview().getId())
                .status(evaluation.getStatus().name())
                .overallScore(evaluation.getOverallScore())
                .technicalScore(evaluation.getTechnicalScore())
                .communicationScore(evaluation.getCommunicationScore())
                .problemSolvingScore(evaluation.getProblemSolvingScore())
                .confidenceScore(evaluation.getConfidenceScore())
                .recommendation(rec != null ? rec.name() : null)
                .recommendationLabel(rec != null ? rec.getLabel() : null)
                .strengths(evaluation.getStrengths())
                .weaknesses(evaluation.getWeaknesses())
                .improvementRoadmap(evaluation.getImprovementRoadmap())
                .summary(evaluation.getSummary())
                .attemptCount(evaluation.getAttemptCount())
                .errorMessage(evaluation.getErrorMessage())
                .evaluatedAt(evaluation.getEvaluatedAt())
                .questionEvaluations(questions)
                .build();
    }
}
