package com.novahire.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novahire.ai.client.AIProviderClient;
import com.novahire.ai.client.GenerationOptions;
import com.novahire.ai.exception.AIGenerationException;
import com.novahire.ai.prompt.PromptContext;
import com.novahire.ai.prompt.PromptTemplateService;
import com.novahire.ai.prompt.PromptType;
import com.novahire.entity.Interview;
import com.novahire.entity.InterviewQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sprint 5 counterpart to {@link AIService}: build evaluation prompt → call provider → parse
 * normalized JSON → return an in-memory {@link EvaluationResult}.
 *
 * <p>Provider-agnostic — depends on {@link AIProviderClient}, not on any concrete provider, so
 * future providers plug in with no change here. Like {@link AIService}, it does NOT persist:
 * the caller ({@code EvaluationService}) owns the transaction and maps the result to entities.
 *
 * <p>One batched call scores every answer and produces the overall report together, which keeps
 * the overall category scores coherent across answers and gives the caller clean all-or-nothing
 * success/failure semantics for its retry logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIEvaluationService {

    private final AIProviderClient providerClient;
    private final PromptTemplateService promptTemplateService;
    private final AIProperties aiProperties;
    private final ObjectMapper objectMapper;

    /**
     * Evaluates one completed interview.
     *
     * @param interview                the interview being evaluated (role/level/language/style)
     * @param questions                its questions, ordered — provide {@code expectedAnswer} for scoring
     * @param answerTextByQuestionId   candidate answer text keyed by question id (missing = skipped)
     * @return normalized {@link EvaluationResult} (not persisted)
     * @throws AIGenerationException if the response cannot be parsed into a valid result
     * @throws com.novahire.ai.exception.AIProviderException on provider network/HTTP failure
     */
    public EvaluationResult evaluate(Interview interview,
                                     List<InterviewQuestion> questions,
                                     Map<Long, String> answerTextByQuestionId) {

        String qaBlock = buildQaBlock(questions, answerTextByQuestionId);

        String technologies = interview.getTechnologies() == null || interview.getTechnologies().isEmpty()
                ? "General programming"
                : String.join(", ", interview.getTechnologies());

        Interview.InterviewStyle style = interview.getInterviewStyle() != null
                ? interview.getInterviewStyle()
                : Interview.InterviewStyle.MIXED;

        PromptContext ctx = PromptContext.builder()
                .targetRole(interview.getTargetRole())
                .experienceLevel(interview.getExperienceLevel().name())
                .technologies(technologies)
                .questionCount(questions.size())
                .interviewStyle(style.name())
                .promptVersion(aiProperties.getPrompt().getVersion())
                .questionsAndAnswers(qaBlock)
                .build();

        String prompt = promptTemplateService.render(
                PromptType.INTERVIEW_EVALUATION, interview.getLanguage(), ctx);

        GenerationOptions options = GenerationOptions.builder()
                .temperature(aiProperties.getEvaluation().getTemperature())
                .maxOutputTokens(aiProperties.getEvaluation().getMaxTokens())
                .build();

        log.info("Calling AI provider='{}' to evaluate interview id={} ({} questions)",
                providerClient.providerName(), interview.getId(), questions.size());

        String rawResponse = providerClient.generate(prompt, options);

        EvaluationResult result = parse(rawResponse, interview.getId());
        log.info("AI evaluation parsed for interview id={}: overall={}, recommendation={}, {} question verdicts",
                interview.getId(), result.overallScore(), result.recommendation(), result.questions().size());
        return result;
    }

    // ── Prompt building ────────────────────────────────────────────────────────

    /**
     * Renders each question with its expected-answer key and the candidate's answer. The
     * expected answer is used here only — it is never returned to clients.
     */
    private String buildQaBlock(List<InterviewQuestion> questions, Map<Long, String> answerTextByQuestionId) {
        StringBuilder sb = new StringBuilder();
        for (InterviewQuestion q : questions) {
            String answer = answerTextByQuestionId.get(q.getId());
            boolean answered = answer != null && !answer.isBlank();
            sb.append("### Question ").append(q.getOrderIndex())
              .append(" [").append(q.getCategory().name()).append("]\n")
              .append("Question: ").append(q.getText() != null ? q.getText().trim() : "").append("\n")
              .append("Expected answer key points: ")
              .append(q.getExpectedAnswer() != null && !q.getExpectedAnswer().isBlank()
                      ? q.getExpectedAnswer().trim()
                      : "(none provided — judge on general correctness for the role)").append("\n")
              .append("Candidate answer: ")
              .append(answered ? answer.trim() : "(no answer submitted — score 0)").append("\n\n");
        }
        return sb.toString().trim();
    }

    // ── Parsing ────────────────────────────────────────────────────────────────

    private EvaluationResult parse(String rawResponse, Long interviewId) {
        String json = extractJsonObject(rawResponse);
        AiEvaluationDto dto;
        try {
            dto = objectMapper.readValue(json, AiEvaluationDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI evaluation for interview id={}: {}", interviewId, json);
            throw new AIGenerationException(
                    "AI returned invalid JSON for interview id=" + interviewId + ": " + e.getMessage(), e);
        }
        if (dto == null) {
            throw new AIGenerationException("AI returned an empty evaluation for interview id=" + interviewId);
        }

        List<EvaluationResult.QuestionResult> questions = new ArrayList<>();
        if (dto.questions() != null) {
            for (AiQuestionEvalDto q : dto.questions()) {
                questions.add(new EvaluationResult.QuestionResult(
                        q.orderIndex() != null ? q.orderIndex() : questions.size(),
                        clamp(q.score(), 0, 10),
                        nz(q.strengths()),
                        nz(q.weaknesses()),
                        nz(q.improvementSuggestions()),
                        nz(q.explanation())
                ));
            }
        }
        if (questions.isEmpty()) {
            throw new AIGenerationException(
                    "AI evaluation contained no per-question verdicts for interview id=" + interviewId);
        }

        return new EvaluationResult(
                clamp(dto.overallScore(), 0, 100),
                clamp(dto.technicalScore(), 0, 100),
                clamp(dto.communicationScore(), 0, 100),
                clamp(dto.problemSolvingScore(), 0, 100),
                clamp(dto.confidenceScore(), 0, 100),
                dto.recommendation(),
                cleanList(dto.strengths()),
                cleanList(dto.weaknesses()),
                cleanList(dto.improvementRoadmap()),
                nz(dto.summary()),
                questions
        );
    }

    /**
     * Strips markdown code fences and isolates the JSON object bounds. Mirrors the tolerant
     * extraction in {@link AIService} — Gemini sometimes wraps JSON in prose/fences.
     */
    private String extractJsonObject(String raw) {
        if (raw == null) return "{}";
        String text = raw.trim();
        if (text.startsWith("```")) {
            int firstNewline = text.indexOf('\n');
            int lastFence = text.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                text = text.substring(firstNewline + 1, lastFence).trim();
            }
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            text = text.substring(start, end + 1);
        }
        return text;
    }

    private int clamp(Integer value, int min, int max) {
        if (value == null) return min;
        return Math.max(min, Math.min(max, value));
    }

    private String nz(String s) {
        return s != null ? s.trim() : "";
    }

    private List<String> cleanList(List<String> in) {
        List<String> out = new ArrayList<>();
        if (in != null) {
            for (String s : in) {
                if (s != null && !s.isBlank()) out.add(s.trim());
            }
        }
        return out;
    }

    // ── Internal DTOs for JSON deserialization ─────────────────────────────────

    private record AiEvaluationDto(
            @JsonProperty("overallScore")        Integer overallScore,
            @JsonProperty("technicalScore")      Integer technicalScore,
            @JsonProperty("communicationScore")  Integer communicationScore,
            @JsonProperty("problemSolvingScore") Integer problemSolvingScore,
            @JsonProperty("confidenceScore")     Integer confidenceScore,
            @JsonProperty("recommendation")      String recommendation,
            @JsonProperty("strengths")           List<String> strengths,
            @JsonProperty("weaknesses")          List<String> weaknesses,
            @JsonProperty("improvementRoadmap")  List<String> improvementRoadmap,
            @JsonProperty("summary")             String summary,
            @JsonProperty("questions")           List<AiQuestionEvalDto> questions
    ) {}

    private record AiQuestionEvalDto(
            @JsonProperty("orderIndex")             Integer orderIndex,
            @JsonProperty("score")                  Integer score,
            @JsonProperty("strengths")              String strengths,
            @JsonProperty("weaknesses")             String weaknesses,
            @JsonProperty("improvementSuggestions") String improvementSuggestions,
            @JsonProperty("explanation")            String explanation
    ) {}
}
