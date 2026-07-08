package com.novahire.ai.client;

/**
 * Abstraction over any LLM HTTP endpoint.
 * Swap the active implementation via {@code novahire.ai.provider} config — no business logic changes.
 */
public interface AIProviderClient {

    /**
     * Sends {@code prompt} to the provider using its configured defaults and returns the raw text.
     *
     * @throws com.novahire.ai.exception.AIProviderException on network error, timeout, or HTTP 4xx/5xx
     */
    String generate(String prompt);

    /**
     * Sends {@code prompt} with per-call {@link GenerationOptions} overrides (temperature, token
     * budget). Providers that cannot honor an override fall back to their default for that field.
     *
     * <p>Default implementation ignores the options and delegates to {@link #generate(String)} so
     * existing providers keep working without change; providers override it to apply the overrides.
     */
    default String generate(String prompt, GenerationOptions options) {
        return generate(prompt);
    }

    /** Stable identifier used for logging and audit. */
    String providerName();
}
