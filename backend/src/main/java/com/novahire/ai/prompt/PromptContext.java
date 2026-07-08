package com.novahire.ai.prompt;

import lombok.Builder;

/**
 * All variables that can be interpolated into a prompt template.
 * Add fields here when new templates need new variables; existing templates ignore unused ones,
 * and {@link PromptTemplateService} treats any absent value as an empty string.
 */
@Builder
public record PromptContext(
        // ── Question generation (Sprint 4) ──
        String targetRole,
        String experienceLevel,
        String technologies,
        int questionCount,
        String interviewStyle,
        String categoryInstructions,
        String promptVersion,
        // ── Interview evaluation (Sprint 5) ──
        /** Pre-rendered block of each question, its expected-answer key, and the candidate's answer. */
        String questionsAndAnswers
) {}
