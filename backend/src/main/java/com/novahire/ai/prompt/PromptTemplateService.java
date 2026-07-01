package com.novahire.ai.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads prompt templates from {@code src/main/resources/prompts/} once, caches them,
 * and interpolates {@code {{variable}}} placeholders with values from a {@link PromptContext}.
 *
 * <p>Language fallback: if the requested language template is missing, English is used.
 * This ensures interviews never fail due to a missing translation.
 */
@Service
@Slf4j
public class PromptTemplateService {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String render(PromptType type, String language, PromptContext ctx) {
        String langKey = toLangKey(language);
        String template = resolveTemplate(type, langKey);
        return interpolate(template, ctx);
    }

    private String resolveTemplate(PromptType type, String langKey) {
        String path = "prompts/" + type.getFilePrefix() + "-" + langKey + ".md";
        return cache.computeIfAbsent(path, p -> {
            String content = loadFromClasspath(p);
            if (content == null && !"en".equals(langKey)) {
                log.warn("Prompt template '{}' not found, falling back to English", p);
                String fallbackPath = "prompts/" + type.getFilePrefix() + "-en.md";
                content = loadFromClasspath(fallbackPath);
                if (content != null) cache.put(fallbackPath, content);
            }
            if (content == null) {
                throw new IllegalStateException("Missing required prompt template: " + p);
            }
            return content;
        });
    }

    private String loadFromClasspath(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) return null;
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read prompt template '{}'", path, e);
            return null;
        }
    }

    private String interpolate(String template, PromptContext ctx) {
        return template
                .replace("{{targetRole}}",          ctx.targetRole())
                .replace("{{experienceLevel}}",      ctx.experienceLevel())
                .replace("{{technologies}}",         ctx.technologies())
                .replace("{{questionCount}}",        String.valueOf(ctx.questionCount()))
                .replace("{{interviewStyle}}",       ctx.interviewStyle())
                .replace("{{categoryInstructions}}", ctx.categoryInstructions())
                .replace("{{promptVersion}}",        ctx.promptVersion());
    }

    private String toLangKey(String language) {
        if (language == null) return "en";
        return switch (language.toUpperCase()) {
            case "FRENCH"  -> "fr";
            case "ARABIC"  -> "ar";
            default        -> "en";
        };
    }
}
