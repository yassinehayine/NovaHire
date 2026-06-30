package com.novahire.ai.exception;

/** Thrown when the LLM provider HTTP call fails (network error, timeout, 4xx/5xx). */
public class AIProviderException extends RuntimeException {
    public AIProviderException(String message) { super(message); }
    public AIProviderException(String message, Throwable cause) { super(message, cause); }
}
