package com.novahire.ai.exception;

/** Thrown when the AI response is received but cannot be parsed into valid questions. */
public class AIGenerationException extends RuntimeException {
    public AIGenerationException(String message) { super(message); }
    public AIGenerationException(String message, Throwable cause) { super(message, cause); }
}
