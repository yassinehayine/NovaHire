package com.novahire.service.question;

import com.novahire.ai.AIProperties;
import com.novahire.ai.AIService;
import com.novahire.entity.Interview;
import com.novahire.entity.InterviewQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Sprint 4 primary implementation of {@link QuestionGenerationStrategy}.
 *
 * <p>Delegates to {@link AIService} (Gemini) when the provider is configured and the API key
 * is present. Falls back silently to {@link StaticQuestionGenerationStrategy} when:
 * <ul>
 *   <li>{@code novahire.ai.provider} is not "gemini"</li>
 *   <li>{@code GEMINI_API_KEY} environment variable is blank</li>
 *   <li>The Gemini API call fails (timeout, rate-limit, parse error, etc.)</li>
 * </ul>
 *
 * <p>This means the application always produces an interview — the AI path is purely additive.
 * Sprint 3 behaviour is preserved as the fallback.
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class GeminiQuestionGenerationStrategy implements QuestionGenerationStrategy {

    private final AIService aiService;
    private final StaticQuestionGenerationStrategy staticFallback;
    private final AIProperties aiProperties;

    @Override
    public List<InterviewQuestion> generateQuestions(Interview interview) {
        if (!isAIEnabled()) {
            log.info("AI provider disabled (provider='{}', key present={}), using static questions for interview id={}",
                    aiProperties.getProvider(),
                    StringUtils.hasText(aiProperties.getGemini().getApiKey()),
                    interview.getId());
            return staticFallback.generateQuestions(interview);
        }

        try {
            List<InterviewQuestion> questions = aiService.generateQuestions(interview);
            log.info("AI generated {} questions for interview id={}", questions.size(), interview.getId());
            return questions;
        } catch (Exception e) {
            log.warn("AI generation failed for interview id={}, falling back to static questions. Reason: {}",
                    interview.getId(), e.getMessage());
            return staticFallback.generateQuestions(interview);
        }
    }

    private boolean isAIEnabled() {
        return "gemini".equalsIgnoreCase(aiProperties.getProvider())
                && StringUtils.hasText(aiProperties.getGemini().getApiKey());
    }
}
