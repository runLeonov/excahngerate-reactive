package com.example.demo;

import com.example.demo.entity.Currency;
import com.example.demo.entity.CurrencyRate;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.repository.CurrencyRepository;
import com.example.demo.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class CurrencyServiceTests {


    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private CurrencyRepository currencyRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void getCurrencyRateForCurrency_ShouldReturnCurrency() {
        String currencyCode = "USD";
        String currencyCodeFrom = "NIO";
        Currency currency = createCurrency(currencyCode);
        when(currencyRepository.findCurrencyRateByIdAndCurrencyCode(currencyCode, currencyCodeFrom))
                .thenReturn(Mono.just(currency));

        Mono<Currency> result = currencyService.getCurrencyRateForCurrency(currencyCode, currencyCodeFrom);

        StepVerifier.create(result)
                .expectNext(currency)
                .verifyComplete();
    }

    @Test
    public void getCurrenciesRates_ShouldReturnAllCurrencies() {
        List<Currency> currencies = createCurrencyList();
        when(currencyRepository.findAll()).thenReturn(Flux.fromIterable(currencies));

        Flux<Currency> result = currencyService.getCurrenciesRates();

        result
                .as(StepVerifier::create)
                .expectNextSequence(currencies)
                .verifyComplete();
    }

    @Test
    public void reinsert_ShouldReturnSavedCurrency() {
        String currencyCode = "USD";
        Currency existingCurrency = createCurrency(currencyCode);
        Currency newCurrency = createCurrency(currencyCode);
        when(currencyRepository.findById(currencyCode)).thenReturn(Mono.just(existingCurrency));
        when(currencyRepository.save(existingCurrency)).thenReturn(Mono.just(existingCurrency));

        Mono<Currency> result = currencyService.reinsert(newCurrency);

        result
                .as(StepVerifier::create)
                .expectNext(existingCurrency)
                .verifyComplete();
    }

    private Currency createCurrency(String currencyCode) {
        Currency currency = new Currency();
        currency.setBasicCode(currencyCode);
        List<CurrencyRate> currencyRates = new ArrayList<>();
        currencyRates.add(createCurrencyRate());
        currency.setCurrenciesRates(currencyRates);
        return currency;
    }

    private List<Currency> createCurrencyList() {
        List<Currency> currencies = new ArrayList<>();
        currencies.add(createCurrency("USD"));
        currencies.add(createCurrency("EUR"));
        return currencies;
    }

    private CurrencyRate createCurrencyRate() {
        CurrencyRate currencyRate = new CurrencyRate();
        currencyRate.setCurrencyExchangeCode("NIO");
        currencyRate.setExchangeRates(createExchangeRates());
        return currencyRate;
    }

    private List<ExchangeRate> createExchangeRates() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setExchangeRate(1.0);
        exchangeRate.setUpdateTime(LocalDateTime.now());
        exchangeRates.add(exchangeRate);
        return exchangeRates;
    }
}