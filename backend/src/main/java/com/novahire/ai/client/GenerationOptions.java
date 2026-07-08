package com.novahire.ai.client;

import lombok.Builder;

/**
 * Provider-agnostic per-call generation overrides. Any field left null means "use the provider's
 * configured default", so callers only set what they need (e.g. evaluation lowers temperature and
 * raises the output budget without touching question-generation defaults).
 */
@Builder
public record GenerationOptions(
        Double temperature,
        Integer maxOutputTokens
) {}
