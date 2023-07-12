package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final String API_KEY = "a5d767ac6c99675cab2f4659";
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(API_BASE_URL)
                .build();
    }
}