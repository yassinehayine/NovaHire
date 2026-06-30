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
        factory.setReadTimeout(properties.getGemini().getTimeoutSeconds() * 1_000);
        return new RestTemplate(factory);
    }
}
