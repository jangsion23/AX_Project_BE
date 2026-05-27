package com.knuaf.chickenstock.global.config.WebSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.ObjectMapper; // 올바른 경로로 수정

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl("https://openapivts.koreainvestment.com:29443").build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}