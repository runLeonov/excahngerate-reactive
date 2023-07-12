package com.example.demo;

import com.example.demo.controller.CurrencyController;
import com.example.demo.entity.Currency;
import com.example.demo.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class CurrencyControllerTests {

    @InjectMocks
    private CurrencyController currencyController;

    @Mock
    private CurrencyService currencyService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(currencyController).build();
    }

    @Test
    public void getCurrencyRateByCodes_ValidCodes_ShouldReturnCurrency() {
        String codeTo = "USD";
        String codeFrom = "NIO";
        Currency currency = createCurrency(codeTo);
        when(currencyService.getCurrencyRateForCurrency(codeTo, codeFrom)).thenReturn(Mono.just(currency));

        webTestClient.get()
                .uri("/currencies/{codeTo}/rate/{codeFrom}", codeTo, codeFrom)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Currency.class)
                .isEqualTo(currency);
    }

    @Test
    public void getCurrencyRateByCodes_InvalidCodes_ShouldReturnNotFound() {
        String codeTo = "USD";
        String codeFrom = "NIO";
        when(currencyService.getCurrencyRateForCurrency(codeTo, codeFrom)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/currencies/{codeTo}/rate/{codeFrom}", codeTo, codeFrom)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getCurrencyRate_ValidCode_ShouldReturnCurrency() {
        // Arrange
        String code = "USD";
        Currency currency = createCurrency(code);
        when(currencyService.getCurrencyRate(code)).thenReturn(Mono.just(currency));

        // Act & Assert
        webTestClient.get()
                .uri("/currencies/{code}", code)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Currency.class)
                .isEqualTo(currency);
    }

    @Test
    public void getCurrencyRate_InvalidCode_ShouldReturnNotFound() {
        // Arrange
        String code = "USD";
        when(currencyService.getCurrencyRate(code)).thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.get()
                .uri("/currencies/{code}", code)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getCurrencyRate_ShouldReturnAllCurrencies() {
        // Arrange
        List<Currency> currencies = createCurrencyList();
        when(currencyService.getCurrenciesRates()).thenReturn(Flux.fromIterable(currencies));

        // Act & Assert
        webTestClient.get()
                .uri("/currencies")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Currency.class)
                .isEqualTo(currencies);
    }

    private Currency createCurrency(String code) {
        Currency currency = new Currency();
        currency.setBasicCode(code);
        return currency;
    }

    private List<Currency> createCurrencyList() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(createCurrency("USD"));
        currencies.add(createCurrency("EUR"));
        return currencies;
    }
}