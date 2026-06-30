package com.novahire.ai.prompt;

import lombok.Builder;

/**
 * All variables that can be interpolated into a prompt template.
 * Add fields here when new templates need new variables; existing templates ignore unused ones.
 */
@Builder
public record PromptContext(
        String targetRole,
        String experienceLevel,
        String technologies,
        int questionCount,
        String interviewStyle,
        String categoryInstructions,
        String promptVersion
) {}
