package com.xm.controller;

import com.xm.model.dto.CryptoMetricsDto;
import com.xm.service.CryptoRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/crypto/recommendations", produces = "application/json")
@RequiredArgsConstructor
public class CryptoRecommendationController {

    private final CryptoRecommendationService cryptoRecommendationService;

    @GetMapping(path = "/metrics/{cryptoId}")
    public ResponseEntity<CryptoMetricsDto> getCryptoMetrics(
            @PathVariable String cryptoId) {

        return ResponseEntity.ok(cryptoRecommendationService.getCryptocurrencyMetrics(cryptoId));
    }

    @GetMapping(path = "/metrics/normalizedRange/desc")
    public ResponseEntity<List<CryptoMetricsDto>> getCryptoMetricsByDescendingNormalizedRange() {

        return ResponseEntity.ok(cryptoRecommendationService.getCryptoMetricsByDescendingNormalizedRange());
    }

    @GetMapping(path = "/metrics/highestNormalizedRange/date/{date}")
    public ResponseEntity<CryptoMetricsDto> getHighestNormalizedRangeByDate(
            @PathVariable String date) {

        return ResponseEntity.ok(cryptoRecommendationService.getHighestNormalizedRangeByDate(date));
    }
}
