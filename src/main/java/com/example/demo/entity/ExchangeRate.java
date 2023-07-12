package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
    @JsonProperty("exchangeRate")
    private Double exchangeRate;
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}
