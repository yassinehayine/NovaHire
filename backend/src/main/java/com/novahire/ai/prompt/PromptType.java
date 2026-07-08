package com.novahire.ai.prompt;

/** Each value maps to a classpath resource: {@code prompts/<filePrefix>-<lang>.md}. */
public enum PromptType {
    QUESTION_GENERATION("question-generation"),
    // Sprint 5 — answer evaluation & interview report
    INTERVIEW_EVALUATION("interview-evaluation");

    private final String filePrefix;

    PromptType(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFilePrefix() {
        return filePrefix;
    }
}
