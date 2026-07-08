package com.novahire.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(AIProperties.class)
public class AIConfig {

    @Bean("aiRestTemplate")
    public RestTemplate aiRestTemplate(AIProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        // Shared by generation and the (slower) evaluation call — use the larger of the two budgets.
        int readTimeoutSeconds = Math.max(
                properties.getGemini().getTimeoutSeconds(),
                properties.getEvaluation().getTimeoutSeconds());
        factory.setReadTimeout(readTimeoutSeconds * 1_000);
        return new RestTemplate(factory);
    }
}
