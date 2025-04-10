package com.tcsintern.PolicyInsight.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String getGeminiApiUrl() {
        return geminiApiUrl;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public String getGeminiApiFullUrl() {
        return geminiApiUrl + "?key=" + geminiApiKey;
    }
}