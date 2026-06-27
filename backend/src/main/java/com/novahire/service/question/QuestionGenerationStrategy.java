package com.novahire.service.question;

import com.novahire.entity.Interview;
import com.novahire.entity.InterviewQuestion;

import java.util.List;

/**
 * Builds the question set for a session. Sprint 3's only implementation is
 * {@link StaticQuestionGenerationStrategy}; Sprint 4 adds an AI-backed implementation
 * (reading targetRole/technologies/CV text) as a drop-in bean — no caller changes needed.
 *
 * <p>Implementations build but do not persist the returned entities — the caller saves them
 * inside the same transaction that transitions the session to IN_PROGRESS.
 */
public interface QuestionGenerationStrategy {

    List<InterviewQuestion> generateQuestions(Interview interview);
}
