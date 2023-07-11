package com.example.demo.service;

import com.example.demo.dto.ExchangeRateDTO;
import com.example.demo.entity.Currency;
import com.example.demo.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

@Service
@Slf4j
public class CurrencyService {
    private final WebClient webClient;
    private final CurrencyRepository repository;

    @Autowired
    public CurrencyService(WebClient webClient, CurrencyRepository currencyRateRepo) {
        this.webClient = webClient;
        this.repository = currencyRateRepo;
    }

    public Mono<Currency> getCurrencyRate(String currencyCode) {
        return repository.findByCurrencyCode(currencyCode)
                .switchIfEmpty(updateCurrencyRate(currencyCode));
    }

    public Flux<Currency> getCurrenciesRates() {
        return repository.findAll();
    }

    public Mono<Currency> updateCurrencyRate(String currencyCode) {
        log.info("Reaching API to get ExchangeRate for " + currencyCode);
        return fetchExchangeRate(currencyCode)
                .flatMap(exchangeRateDto -> convertToCurrencyRate(exchangeRateDto, currencyCode));
    }

    private Mono<ExchangeRateDTO> fetchExchangeRate(String currencyCode) {
        return webClient.get()
                .uri("/latest/USD")
                .retrieve()
                .bodyToMono(ExchangeRateDTO.class);
    }

    private Mono<Currency> convertToCurrencyRate(ExchangeRateDTO exchangeRateDto, String currencyCode) {
        return convertToCurrencyRateFlux(Mono.just(exchangeRateDto))
                .filter(x -> x.getCurrencyCode().equals(currencyCode))
                .singleOrEmpty();
    }

    public Flux<Currency> reinsert(Currency currency) {
        Mono<Void> deleteMono = repository.delete(currency)
                .onErrorResume(error -> {
                    return Mono.empty();
                });
        Mono<Currency> saveMono = repository.insertCurrency(currency);
        return Flux.concat(deleteMono.thenMany(Flux.empty()), saveMono);
    }


    public Flux<Currency> convertToCurrencyRateFlux(Mono<ExchangeRateDTO> exchangeRateDtoMono) {
        return exchangeRateDtoMono.flatMapMany(exchange -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(exchange.getUpdateTimeStr(), formatter);
            return Flux.fromIterable(exchange.getRates().entrySet())
                    .map(entry ->
                            ExchangeRateDTO.mapToCurrency(entry, dateTime)
                    );
        });
    }

}