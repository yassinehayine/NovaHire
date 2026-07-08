package com.novahire.dto.response;

import com.novahire.entity.InterviewQuestion;
import com.novahire.entity.QuestionEvaluation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-question verdict for the review UI. Deliberately excludes {@code expectedAnswer} — the model
 * answer key is server-side only and never leaves the backend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEvaluationResponse {

    private Long questionId;
    private Integer orderIndex;
    private String questionText;
    private String category;
    /** The candidate's raw answer (from InterviewAnswer), for side-by-side review. */
    private String candidateAnswer;
    /** 0–10. */
    private Integer score;
    private String strengths;
    private String weaknesses;
    private String improvementSuggestions;
    private String explanation;

    public static QuestionEvaluationResponse from(QuestionEvaluation qe, String candidateAnswer) {
        InterviewQuestion q = qe.getQuestion();
        return QuestionEvaluationResponse.builder()
                .questionId(q.getId())
                .orderIndex(q.getOrderIndex())
                .questionText(q.getText())
                .category(q.getCategory().name())
                .candidateAnswer(candidateAnswer)
                .score(qe.getScore())
                .strengths(qe.getStrengths())
                .weaknesses(qe.getWeaknesses())
                .improvementSuggestions(qe.getImprovementSuggestions())
                .explanation(qe.getExplanation())
                .build();
    }
}
