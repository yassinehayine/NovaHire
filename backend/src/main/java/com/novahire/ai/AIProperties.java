package com.novahire.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "novahire.ai")
@Getter
@Setter
public class AIProperties {

    /** Active provider: "gemini" or "static". Defaults to "gemini". */
    private String provider = "gemini";

    /** If true and AI fails (missing key, timeout, parse error), fall back to static questions. */
    private boolean fallbackToStatic = true;

    private Prompt prompt = new Prompt();
    private Gemini gemini = new Gemini();

    @Getter
    @Setter
    public static class Prompt {
        private String version = "v1";
    }

    @Getter
    @Setter
    public static class Gemini {
        private String apiKey = "";
        private String model = "gemini-1.5-flash";
        private double temperature = 0.7;
        private int maxTokens = 2048;
        private int timeoutSeconds = 30;
    }
}
