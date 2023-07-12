package com.example.demo.dto;

import com.example.demo.entity.Currency;
import com.example.demo.entity.CurrencyRate;
import com.example.demo.entity.ExchangeRate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ExchangeRateDTO {
    @JsonProperty("base_code")
    private String baseCurrency;
    @JsonProperty("conversion_rates")
    private Map<String, Double> rates;
    @JsonProperty("time_last_update_utc")
    private String updateTimeStr;


    public static Currency mapToCurrency(ExchangeRateDTO exchangeRateDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(exchangeRateDTO.getUpdateTimeStr(), formatter);

        Currency currency = Currency.builder()
                .basicCode(exchangeRateDTO.getBaseCurrency())
                .build();

        List<CurrencyRate> currencyRates = exchangeRateDTO.getRates().entrySet().stream()
                .map(entry -> {
                    ExchangeRate exchangeRate = ExchangeRate.builder()
                            .exchangeRate(entry.getValue())
                            .updateTime(dateTime)
                            .build();
                    return CurrencyRate.builder()
                            .currencyExchangeCode(entry.getKey())
                            .lastExchangeRate(exchangeRate)
                            .exchangeRates(Collections.singletonList(exchangeRate))
                            .build();
                })
                .collect(Collectors.toList());

        currency.setCurrenciesRates(currencyRates);
        return currency;
    }
}