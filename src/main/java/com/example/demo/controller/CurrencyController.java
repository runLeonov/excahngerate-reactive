package com.example.demo.controller;

import com.example.demo.entity.Currency;
import com.example.demo.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("/currency")
@AllArgsConstructor
public class CurrencyController {
    private final CurrencyService service;

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
