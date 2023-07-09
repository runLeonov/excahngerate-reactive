package com.example.demo;

import com.example.demo.entity.Currency;
import com.example.demo.repository.CurrencyRepository;
import com.example.demo.service.CurrencyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyServiceTests {

    @Mock
    private CurrencyRepository currencyRepository;

    private CurrencyService currencyService;

    @Before
    public void setUp() {
        currencyService = new CurrencyService(currencyRepository);
    }

    @Test
    public void testGetCurrencyRate_ReturnsCurrencyFromRepository_WhenFound() {
        String currencyCode = "USD";
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        when(currencyRepository.findByCode(currencyCode)).thenReturn(Mono.just(currency));

        currencyService.getCurrencyRate(currencyCode).subscribe(
                res-> {
                    assertNotNull(res);
                    assertEquals(currency, res);
                }
        );
    }

    @Test
    public void testGetCurrencyRate_CallsUpdateCurrencyRate_WhenNotFoundInRepository() {
        String currencyCode = "EUR";
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        when(currencyRepository.findByCode(currencyCode)).thenReturn(Mono.empty());
        when(currencyRepository.insertOrUpdate(currency)).thenReturn(Mono.just(currency));

        currencyService.getCurrencyRate(currencyCode).subscribe(
                res-> {
                    assertNotNull(res);
                    assertEquals(currency, res);
                }
        );

        verify(currencyRepository, times(1)).insertOrUpdate(currency);
    }

    @Test
    public void testGetCurrenciesRates_ReturnsAllCurrenciesFromRepository() {
        Currency currency1 = new Currency();
        Currency currency2 = new Currency();
        when(currencyRepository.findAll()).thenReturn(Flux.just(currency1, currency2));

        Flux<Currency> result = currencyService.getCurrenciesRates();

        StepVerifier.create(result)
                .expectNext(currency1)
                .expectNext(currency2)
                .verifyComplete();
    }
}