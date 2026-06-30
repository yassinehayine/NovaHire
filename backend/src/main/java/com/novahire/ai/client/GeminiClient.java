package com.novahire.ai.client;

import com.novahire.ai.AIProperties;
import com.novahire.ai.exception.AIProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Calls the Google Gemini REST API (v1beta generateContent).
 * Uses a dedicated RestTemplate bean with a configurable read timeout.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiClient implements AIProviderClient {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private final AIProperties properties;

    @Qualifier("aiRestTemplate")
    private final RestTemplate restTemplate;

    @Override
    public String generate(String prompt) {
        String url = String.format(GEMINI_URL,
                properties.getGemini().getModel(),
                properties.getGemini().getApiKey());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", properties.getGemini().getTemperature(),
                        "maxOutputTokens", properties.getGemini().getMaxTokens()
                )
        );

        try {
            log.debug("Calling Gemini API model={}", properties.getGemini().getModel());
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);
            String text = extractText(response.getBody());
            log.debug("Gemini API responded with {} chars", text != null ? text.length() : 0);
            return text;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new AIProviderException(
                    "Gemini API returned HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            throw new AIProviderException("Gemini API timeout or network error: " + e.getMessage(), e);
        }
    }

    @Override
    public String providerName() {
        return "gemini";
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String extractText(Map responseBody) {
        try {
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new AIProviderException(
                    "Could not extract text from Gemini response: " + e.getMessage(), e);
        }
    }
}
