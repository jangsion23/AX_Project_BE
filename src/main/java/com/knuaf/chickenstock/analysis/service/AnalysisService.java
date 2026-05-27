package com.knuaf.chickenstock.analysis.service;

import com.knuaf.chickenstock.analysis.dto.StockAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AnalysisService {
    private final WebClient webClient;

    // application.properties에서 URL 주입
    public AnalysisService(WebClient.Builder webClientBuilder, @Value("${ai.server.url}") String aiServerUrl) {
        this.webClient = webClientBuilder.baseUrl(aiServerUrl).build();
    }

    public StockAnalysis generateAnalysis(String symbol) {
        try {
            // 쿼리 파라미터로 symbol 전달
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/analyze").queryParam("symbol", symbol).build())
                    .retrieve()
                    .bodyToMono(StockAnalysis.class)
                    .block();
        } catch (Exception e) {
            log.error("AI 리포트 통신 에러: ", e);
            throw new RuntimeException("AI 분석 데이터를 불러오는데 실패했습니다.");
        }
    }
}