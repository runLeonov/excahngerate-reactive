package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final String API_KEY = "8ec328acbb6864faf3c9b47b";
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(API_BASE_URL)
                .build();
    }
}