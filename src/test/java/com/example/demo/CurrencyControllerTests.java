package com.example.demo;

import com.example.demo.controller.CurrencyController;
import com.example.demo.entity.Currency;
import com.example.demo.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebFluxTest(CurrencyController.class)
public class CurrencyControllerTests {
    @MockBean
    private CurrencyService currencyService;

    @Test
    public void testGetCurrencyRate_ReturnsCurrencySuccessfully() {
        String currencyCode = "USD";
        Currency currency = Currency.builder()
                .currencyCode(currencyCode)
                .exchangeRate(BigDecimal.valueOf(1.0))
                .updateTime(LocalDateTime.now())
                .build();

        Mockito.when(currencyService.getCurrencyRate(currencyCode)).thenReturn(Mono.just(currency));

        WebTestClient
                .bindToController(new CurrencyController(currencyService))
                .build()
                .get()
                .uri("/currency/" + currencyCode)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Currency.class)
                .isEqualTo(currency);
    }

    @Test
    public void testGetCurrencyRate_ReturnsNotFoundWhenCurrencyNotFound() {
        String currencyCode = "EUR";

        Mockito.when(currencyService.getCurrencyRate(currencyCode)).thenReturn(Mono.empty());

        WebTestClient
                .bindToController(new CurrencyController(currencyService))
                .build()
                .get()
                .uri("/currency/" + currencyCode)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testGetCurrenciesRates_ReturnsCurrenciesSuccessfully() {
        Currency currency1 = Currency.builder()
                .currencyCode("USD")
                .exchangeRate(BigDecimal.valueOf(1.0))
                .updateTime(LocalDateTime.now())
                .build();
        Currency currency2 = Currency.builder()
                .currencyCode("EUR")
                .exchangeRate(BigDecimal.valueOf(0.8))
                .updateTime(LocalDateTime.now())
                .build();

        Mockito.when(currencyService.getCurrenciesRates()).thenReturn(Flux.just(currency1, currency2));

        WebTestClient
                .bindToController(new CurrencyController(currencyService))
                .build()
                .get()
                .uri("/currency")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Currency.class)
                .contains(currency1, currency2);
    }
}