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
    private Evaluation evaluation = new Evaluation();

    /**
     * True when a real AI provider is selected and usable (key present). Single source of truth
     * shared by question generation and evaluation — when false, both fall back to their
     * non-AI behavior instead of calling a provider.
     */
    public boolean isProviderConfigured() {
        return "gemini".equalsIgnoreCase(provider)
                && gemini != null
                && gemini.getApiKey() != null
                && !gemini.getApiKey().isBlank();
    }

    @Getter
    @Setter
    public static class Prompt {
        private String version = "v1";
    }

    /**
     * Sprint 5 evaluation tuning. Separate from question generation because scoring wants
     * low-temperature determinism and a larger output budget (one batched call returns every
     * per-question verdict plus the overall report).
     */
    @Getter
    @Setter
    public static class Evaluation {
        /** Engine schema version stored on each report — bump when the eval prompt/logic evolves. */
        private String version = "v1";
        /** Low temperature → consistent, reproducible scoring. */
        private double temperature = 0.2;
        /** Larger than generation: N verdicts + overall report in one response. */
        private int maxTokens = 4096;
        /** A batched evaluation legitimately runs longer than a single generation. */
        private int timeoutSeconds = 60;
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
