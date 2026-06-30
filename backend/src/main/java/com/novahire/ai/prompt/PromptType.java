package com.novahire.ai.prompt;

/** Each value maps to a classpath resource: {@code prompts/<filePrefix>-<lang>.md}. */
public enum PromptType {
    QUESTION_GENERATION("question-generation");

    private final String filePrefix;

    PromptType(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFilePrefix() {
        return filePrefix;
    }
}
