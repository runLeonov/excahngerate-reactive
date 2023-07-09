package com.example.demo.service;

import com.example.demo.entity.Currency;
import com.example.demo.entity.ExchangeRateDTO;
import com.example.demo.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Slf4j
public class CurrencyService {
    private final WebClient webClient;
    private final CurrencyRepository repository;
    private static final String API_KEY = "8ec328acbb6864faf3c9b47b";
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRateRepo) {
        this.webClient = WebClient.builder()
                .baseUrl(API_BASE_URL)
                .build();
        this.repository = currencyRateRepo;
        scheduleCurrencyRateUpdates();
    }

    public Mono<Currency> getCurrencyRate(String currencyCode) {
        return repository.findByCode(currencyCode)
                .switchIfEmpty(updateCurrencyRate(currencyCode));
    }

    public Flux<Currency> getCurrenciesRates() {
        return repository.findAll();
    }

    private Mono<Currency> updateCurrencyRate(String currencyCode) {
        log.info("Reaching API to get ExchangeRate for " + currencyCode);
        Mono<ExchangeRateDTO> exchangeRateDto = webClient.get()
                .uri("/latest/USD")
                .retrieve()
                .bodyToMono(ExchangeRateDTO.class);
        return convertToCurrencyRateFlux(exchangeRateDto)
                .filter(x -> x.getCurrencyCode().equals(currencyCode))
                .singleOrEmpty();
    }


    @Scheduled(fixedRate = 1000000L)
    private void scheduleCurrencyRateUpdates() {
        log.info("Reaching API to get ExchangeRates...");
        Mono<ExchangeRateDTO> exchangeRateDto = webClient.get()
                .uri("/latest/USD")
                .retrieve()
                .bodyToMono(ExchangeRateDTO.class)
                .subscribeOn(Schedulers.boundedElastic());

        convertToCurrencyRateFlux(exchangeRateDto).flatMap(repository::insertOrUpdate).subscribe();
        log.info("Got it:)");
    }


    public Flux<Currency> convertToCurrencyRateFlux(Mono<ExchangeRateDTO> exchangeRateDtoMono) {
        return exchangeRateDtoMono.flatMapMany(exchange -> {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(exchange.getUpdateTimeStr(), formatter);

            return Flux.fromIterable(exchange.getRates().entrySet())
                    .map(entry -> Currency.builder()
                            .currencyCode(entry.getKey())
                            .exchangeRate(BigDecimal.valueOf(entry.getValue()))
                            .updateTime(dateTime)
                            .build()
                    );
        });
    }

}