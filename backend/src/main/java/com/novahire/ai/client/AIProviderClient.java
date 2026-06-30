package com.novahire.ai.client;

/**
 * Abstraction over any LLM HTTP endpoint.
 * Swap the active implementation via {@code novahire.ai.provider} config — no business logic changes.
 */
public interface AIProviderClient {

    /**
     * Sends {@code prompt} to the provider and returns the raw text response.
     *
     * @throws com.novahire.ai.exception.AIProviderException on network error, timeout, or HTTP 4xx/5xx
     */
    String generate(String prompt);

    /** Stable identifier used for logging and audit. */
    String providerName();
}
