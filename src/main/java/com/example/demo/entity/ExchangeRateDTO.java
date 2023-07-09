package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateDTO {
    @JsonProperty("base_code")
    private String baseCurrency;
    @JsonProperty("conversion_rates")
    private Map<String, Double> rates;
    @JsonProperty("time_last_update_utc")
    private String updateTimeStr;
}