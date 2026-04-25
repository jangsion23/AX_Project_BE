package com.knuaf.chickenstock.stock.controller;

import com.knuaf.chickenstock.stock.dto.StockPrice;
import com.knuaf.chickenstock.stock.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @MessageMapping("/subscribe")
    @SendTo("/topic/ack")
    public String handleSubscribe(@Payload Map<String, String> payload) {
        String symbol = payload.get("symbol");
        return symbol + " 구독 시작";
    }

    @GetMapping("/api/stock/{symbol}")
    public ResponseEntity<StockPrice> getInitialPrice(@PathVariable String symbol) {
        // StockService에 구현된 KIS API 현재가 조회 메서드를 호출합니다.
        // (주의: stockService 안에 있는 실제 메서드 이름에 맞춰서 'getCurrentPrice' 부분을 수정해 주세요!)
        StockPrice initialData = stockService.getLatestPrice(symbol);

        return ResponseEntity.ok(initialData);
    }
}