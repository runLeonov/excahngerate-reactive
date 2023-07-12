package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate {
    @JsonProperty("currencyExchangeCode")
    private String currencyExchangeCode;
    @JsonProperty("exchangeRates")
    private List<ExchangeRate> exchangeRates;
    @Transient
    @JsonProperty("lastExchangeRate")
//    @JsonIgnore
    private transient ExchangeRate lastExchangeRate;
}
