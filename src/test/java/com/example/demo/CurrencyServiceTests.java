package com.example.demo;

import com.example.demo.entity.Currency;
import com.example.demo.repository.CurrencyRepository;
import com.example.demo.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class CurrencyServiceTests {

    @Mock
    private WebClient webClient;

    @Mock
    private CurrencyRepository currencyRepository;

    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyService = new CurrencyService(webClient, currencyRepository);
    }

    @Test
    void getCurrenciesRates_ReturnsAllCurrencies() {
        Currency currency1 = new Currency("USD", BigDecimal.valueOf(1.0));
        Currency currency2 = new Currency("EUR", BigDecimal.valueOf(0.8));
        when(currencyRepository.findAll()).thenReturn(Flux.just(currency1, currency2));

        currencyService.getCurrenciesRates().collectList().block();

        verify(currencyRepository).findAll();
        verifyNoMoreInteractions(currencyRepository);
    }


    @Test
    void reinsert_DeletesAndInsertsCurrency() {
        Currency currency = new Currency("USD", BigDecimal.valueOf(1.0));

        when(currencyRepository.delete(currency)).thenReturn(Mono.empty());
        when(currencyRepository.insertCurrency(currency)).thenReturn(Mono.just(currency));

        currencyService.reinsert(currency).collectList().block();

        verify(currencyRepository).delete(currency);
        verify(currencyRepository).insertCurrency(currency);
        verifyNoMoreInteractions(currencyRepository);
    }
}
