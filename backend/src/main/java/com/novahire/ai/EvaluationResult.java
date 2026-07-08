package com.novahire.ai;

import java.util.List;

/**
 * Normalized, provider-agnostic result of one interview evaluation, produced by
 * {@link AIEvaluationService} after parsing the AI response. Carries no raw provider payload —
 * the caller persists these fields into the evaluation entities.
 *
 * <p>Scores are already clamped to their target ranges by the parser: overall/category on 0–100,
 * per-question on 0–10.
 */
public record EvaluationResult(
        int overallScore,
        int technicalScore,
        int communicationScore,
        int problemSolvingScore,
        int confidenceScore,
        String recommendation,
        List<String> strengths,
        List<String> weaknesses,
        List<String> improvementRoadmap,
        String summary,
        List<QuestionResult> questions
) {
    /**
     * Per-question verdict. {@code orderIndex} ties the verdict back to the matching
     * {@link com.novahire.entity.InterviewQuestion} without exposing question identity to the model.
     */
    public record QuestionResult(
            int orderIndex,
            int score,
            String strengths,
            String weaknesses,
            String improvementSuggestions,
            String explanation
    ) {}
}
