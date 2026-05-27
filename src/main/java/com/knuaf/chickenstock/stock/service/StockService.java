package com.knuaf.chickenstock.stock.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuaf.chickenstock.stock.dto.StockPrice;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StockService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kis.api.key}")
    private String apiKey;

    @Value("${kis.api.secret}")
    private String apiSecret;

    private String accessToken;

    public StockService(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 6 * * *")
    public void generateToken() {
        log.info("🔑 한국투자증권 API 토큰 발급 시작...");
        try {
            Map<String, String> body = new HashMap<>();
            body.put("grant_type", "client_credentials");
            body.put("appkey", apiKey);
            body.put("appsecret", apiSecret);

            String response = webClient.post()
                    .uri("/oauth2/tokenP")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(response);
            this.accessToken = rootNode.path("access_token").asText();

            log.info("토큰 발급 성공!");
        } catch (Exception e) {
            log.error("토큰 발급 실패: ", e);
        }
    }

    public StockPrice getLatestPrice(String symbol) {
        try {
            if (accessToken == null) {
                throw new RuntimeException("토큰이 아직 발급되지 않았습니다.");
            }

            // 💡 1. GET 요청 시작 (명시적 타입 선언으로 컴파일러 에러 방지)
            WebClient.RequestHeadersUriSpec<?> uriSpec = webClient.get();

            // 💡 2. URI 세팅
            WebClient.RequestHeadersSpec<?> headersSpec = uriSpec.uri(
                    "/uapi/domestic-stock/v1/quotations/inquire-price?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD={symbol}", symbol
            );

            // 💡 3. 헤더 세팅 (체이닝을 끊고 개별적으로 주입)
            headersSpec.header("authorization", "Bearer " + accessToken);
            headersSpec.header("appkey", apiKey);
            headersSpec.header("appsecret", apiSecret);
            headersSpec.header("tr_id", "FHKST01010100");

            // 💡 4. API 호출 및 응답 받기 (이제 retrieve()를 정상적으로 인식합니다)
            String response = headersSpec.retrieve().bodyToMono(String.class).block();

            // JSON 파싱 로직
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode outputNode = rootNode.path("output");

            long currentPrice = Long.parseLong(outputNode.path("stck_prpr").asText());
            long openPrice = Long.parseLong(outputNode.path("stck_oprc").asText());
            long highPrice = Long.parseLong(outputNode.path("stck_hgpr").asText());
            long lowPrice = Long.parseLong(outputNode.path("stck_lwpr").asText());

            return new StockPrice(
                    symbol,
                    openPrice,
                    highPrice,
                    lowPrice,
                    currentPrice,
                    Instant.now().getEpochSecond()
            );

        } catch (Exception e) {
            log.error("실시간 API 호출 에러: ", e);
            // 에러 시에도 차트 렌더링을 방해하지 않도록 0값 반환
            return new StockPrice(symbol, 0L, 0L, 0L, 0L, Instant.now().getEpochSecond());
        }
    }
}