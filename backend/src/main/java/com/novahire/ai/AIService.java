package com.novahire.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novahire.ai.client.AIProviderClient;
import com.novahire.ai.exception.AIGenerationException;
import com.novahire.ai.prompt.PromptContext;
import com.novahire.ai.prompt.PromptTemplateService;
import com.novahire.ai.prompt.PromptType;
import com.novahire.entity.Interview;
import com.novahire.entity.InterviewQuestion;
import com.novahire.entity.InterviewQuestion.QuestionCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Orchestrates AI-powered question generation:
 * build prompt → call provider → parse JSON → map to entities.
 *
 * <p>This service is provider-agnostic: it depends on {@link AIProviderClient},
 * not on any specific implementation. Swapping providers requires only a config change.
 *
 * <p>This service does NOT persist entities — the caller (inside its own transaction) saves them.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final AIProviderClient providerClient;
    private final PromptTemplateService promptTemplateService;
    private final AIProperties aiProperties;
    private final ObjectMapper objectMapper;

    /**
     * Generates interview questions using the configured AI provider.
     *
     * @param interview the interview session providing all generation context
     * @return ordered list of {@link InterviewQuestion} entities (not yet persisted)
     * @throws AIGenerationException if the AI response cannot be parsed into valid questions
     */
    public List<InterviewQuestion> generateQuestions(Interview interview) {
        String promptId = UUID.randomUUID().toString();

        Interview.InterviewStyle style = interview.getInterviewStyle() != null
                ? interview.getInterviewStyle()
                : Interview.InterviewStyle.MIXED;

        String technologies = interview.getTechnologies() == null || interview.getTechnologies().isEmpty()
                ? "General programming"
                : String.join(", ", interview.getTechnologies());

        PromptContext ctx = PromptContext.builder()
                .targetRole(interview.getTargetRole())
                .experienceLevel(interview.getExperienceLevel().name())
                .technologies(technologies)
                .questionCount(interview.getQuestionCount())
                .interviewStyle(style.name())
                .categoryInstructions(resolveCategoryInstructions(style))
                .promptVersion(aiProperties.getPrompt().getVersion())
                .build();

        String prompt = promptTemplateService.render(
                PromptType.QUESTION_GENERATION, interview.getLanguage(), ctx);

        // Store the prompt for debugging/audit — entity is in-session, dirty-checked on commit
        interview.setAiPromptSnapshot(prompt);

        log.info("Calling AI provider='{}' for interview id={} promptId={}",
                providerClient.providerName(), interview.getId(), promptId);

        String rawResponse = providerClient.generate(prompt);

        List<AiQuestionDto> parsed = parseQuestions(rawResponse, interview.getId());

        if (parsed.isEmpty()) {
            throw new AIGenerationException(
                    "AI provider returned an empty question list for interview id=" + interview.getId());
        }

        log.info("AI provider returned {} questions for interview id={}", parsed.size(), interview.getId());

        return mapToEntities(interview, parsed, promptId);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private String resolveCategoryInstructions(Interview.InterviewStyle style) {
        return switch (style) {
            case MIXED ->
                    "TECHNICAL: ~30%, BEHAVIORAL: ~20%, PROBLEM_SOLVING: ~15%, " +
                    "ALGORITHMS: ~15%, SYSTEM_DESIGN: ~10%, COMMUNICATION: ~10%";
            case TECHNICAL ->
                    "TECHNICAL: ~30%, ALGORITHMS: ~25%, CODING: ~25%, DEBUGGING: ~20%";
            case BEHAVIORAL ->
                    "BEHAVIORAL: ~45%, COMMUNICATION: ~30%, PROBLEM_SOLVING: ~25%";
            case SYSTEM_DESIGN ->
                    "SYSTEM_DESIGN: ~40%, ARCHITECTURE: ~25%, TECHNICAL: ~25%, BEHAVIORAL: ~10%";
        };
    }

    private List<AiQuestionDto> parseQuestions(String rawResponse, Long interviewId) {
        String json = extractJsonArray(rawResponse);
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI response for interview id={}: {}", interviewId, json);
            throw new AIGenerationException(
                    "AI returned invalid JSON for interview id=" + interviewId + ": " + e.getMessage(), e);
        }
    }

    /**
     * Strips markdown code fences ({@code ```json...```}) and finds the JSON array bounds.
     * Gemini sometimes wraps its response in markdown even when instructed not to.
     */
    private String extractJsonArray(String raw) {
        String text = raw.trim();

        // Strip ```json ... ``` or ``` ... ```
        if (text.startsWith("```")) {
            int firstNewline = text.indexOf('\n');
            int lastFence    = text.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                text = text.substring(firstNewline + 1, lastFence).trim();
            }
        }

        // Find the JSON array within any surrounding prose
        int arrayStart = text.indexOf('[');
        int arrayEnd   = text.lastIndexOf(']');
        if (arrayStart >= 0 && arrayEnd > arrayStart) {
            text = text.substring(arrayStart, arrayEnd + 1);
        }

        return text;
    }

    private List<InterviewQuestion> mapToEntities(Interview interview,
                                                   List<AiQuestionDto> dtos,
                                                   String promptId) {
        int count = Math.min(dtos.size(), interview.getQuestionCount());
        List<InterviewQuestion> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            AiQuestionDto dto = dtos.get(i);
            result.add(InterviewQuestion.builder()
                    .interview(interview)
                    .orderIndex(i)
                    .text(dto.text() != null ? dto.text().trim() : "")
                    .category(resolveCategory(dto.category()))
                    .difficulty(interview.getExperienceLevel())
                    .source(InterviewQuestion.QuestionSource.AI_GENERATED)
                    .sourceRef(promptId)            // reuses existing field for prompt traceability
                    .expectedAnswer(dto.expectedAnswer())
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        return result;
    }

    private QuestionCategory resolveCategory(String raw) {
        if (raw == null) return QuestionCategory.TECHNICAL;
        try {
            return QuestionCategory.valueOf(raw.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            log.debug("Unknown AI category '{}', defaulting to TECHNICAL", raw);
            return QuestionCategory.TECHNICAL;
        }
    }

    // ── Internal DTO for JSON deserialization ──────────────────────────────────

    private record AiQuestionDto(
            @JsonProperty("text")           String text,
            @JsonProperty("category")       String category,
            @JsonProperty("expectedAnswer") String expectedAnswer
    ) {}
}
