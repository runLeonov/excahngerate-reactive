package com.example.demo.dto;

import com.example.demo.entity.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ExchangeRateDTO {
    @JsonProperty("base_code")
    private String baseCurrency;
    @JsonProperty("conversion_rates")
    private Map<String, Double> rates;
    @JsonProperty("time_last_update_utc")
    private String updateTimeStr;

    public static Currency mapToCurrency(Map.Entry<String, Double> entry, LocalDateTime updateTime) {
        return Currency.builder()
                .currencyCode(entry.getKey())
                .exchangeRate(BigDecimal.valueOf(entry.getValue()))
                .updateTime(updateTime)
                .build();
    }
}