package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@Table("currency_rate")
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    @Id
    private String currencyCode;
    private BigDecimal exchangeRate;
    private LocalDateTime updateTime;

    public Currency(String currencyCode, BigDecimal exchangeRate) {
        this.currencyCode = currencyCode;
        this.exchangeRate = exchangeRate;
    }
}