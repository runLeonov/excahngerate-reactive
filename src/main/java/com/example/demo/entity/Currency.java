package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Builder
@Data
@Document(collection = "currency_codes")
@AllArgsConstructor
@NoArgsConstructor
public class Currency {
    @Id
    private String basicCode;
    @Field("currenciesRates")
    private List<CurrencyRate> currenciesRates;
}