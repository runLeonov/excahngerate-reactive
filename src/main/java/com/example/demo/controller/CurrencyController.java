package com.example.demo.controller;

import com.example.demo.entity.Currency;
import com.example.demo.entity.CurrencyRate;
import com.example.demo.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("/currencies")
@AllArgsConstructor
public class CurrencyController {
    private final CurrencyService service;

    @GetMapping("/{codeTo}/rate/{codeFrom}")
    public Mono<ResponseEntity<Currency>> getCurrencyRateByCodes(@PathVariable String codeTo, @PathVariable String codeFrom) {
        return service.getCurrencyRateForCurrency(codeTo, codeFrom)
                .map(currency -> ResponseEntity.ok().body(currency))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{code}")
    public Mono<ResponseEntity<Currency>> getCurrencyRate(@PathVariable String code) {
        return service.getCurrencyRate(code)
                .map(currency -> ResponseEntity.ok().body(currency))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Currency> getCurrencyRate() {
        return service.getCurrenciesRates()
                .switchIfEmpty(Flux.fromIterable(new ArrayList<>()));
    }
}
