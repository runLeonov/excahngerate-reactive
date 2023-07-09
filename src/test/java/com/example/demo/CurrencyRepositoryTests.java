package com.example.demo;

import com.example.demo.entity.Currency;
import com.example.demo.repository.CurrencyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.relational.core.query.Query;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyRepositoryTests {

    @Mock
    private R2dbcEntityTemplate entityTemplate;

    private CurrencyRepository currencyRepository;

    @Before
    public void setUp() {
        currencyRepository = new CurrencyRepository(entityTemplate);
    }

    @Test
    public void testFindByCode_ReturnsCurrency_WhenFound() {
        String currencyCode = "USD";
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        when(entityTemplate.selectOne(any(Query.class), eq(Currency.class))).thenReturn(Mono.just(currency));

        Mono<Currency> result = currencyRepository.findByCode(currencyCode);

        assertNotNull(result);
        assertEquals(currency, result.block());
    }

    @Test
    public void testFindByCode_ReturnsEmptyMono_WhenNotFound() {
        String currencyCode = "EUR";
        when(entityTemplate.selectOne(any(Query.class), eq(Currency.class))).thenReturn(Mono.empty());

        Mono<Currency> result = currencyRepository.findByCode(currencyCode);

        assertNotNull(result);
        assertTrue(result instanceof Mono);
        assertNull(result.block());
    }

    @Test
    public void testFindAll_ReturnsAllCurrenciesFromRepository() {
        Currency currency1 = new Currency();
        Currency currency2 = new Currency();
        when(entityTemplate.select(any(Query.class), eq(Currency.class))).thenReturn(Flux.just(currency1, currency2));

        Flux<Currency> result = currencyRepository.findAll();

        StepVerifier.create(result)
                .expectNext(currency1)
                .expectNext(currency2)
                .verifyComplete();
    }

    @Test
    public void testInsertOrUpdate_ReturnsExistingCurrency_WhenUpdateTimeIsEqual() {
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        currency.setUpdateTime(LocalDateTime.now());
        when(entityTemplate.selectOne(any(Query.class), eq(Currency.class))).thenReturn(Mono.just(currency));

        Mono<Currency> result = currencyRepository.insertOrUpdate(currency);

        assertNotNull(result);
        assertEquals(currency, result.block());
        verify(entityTemplate, never()).update(any(Currency.class));
        verify(entityTemplate, never()).insert(any(Currency.class));
    }

    @Test
    public void testInsertOrUpdate_UpdatesAndReturnsCurrency_WhenUpdateTimeIsNotEqual() {
        Currency existingCurrency = new Currency();
        existingCurrency.setCurrencyCode("USD");
        existingCurrency.setUpdateTime(LocalDateTime.now().minusMinutes(1));

        Currency updatedCurrency = new Currency();
        updatedCurrency.setCurrencyCode("USD");
        updatedCurrency.setUpdateTime(LocalDateTime.now());

        when(entityTemplate.selectOne(any(Query.class), eq(Currency.class))).thenReturn(Mono.just(existingCurrency));
        when(entityTemplate.update(any(Currency.class))).thenReturn(Mono.just(updatedCurrency));

        Mono<Currency> result = currencyRepository.insertOrUpdate(updatedCurrency);

        assertNotNull(result);
        assertEquals(updatedCurrency, result.block());
        verify(entityTemplate, times(1)).update(updatedCurrency);
        verify(entityTemplate, never()).insert(any(Currency.class));
    }

    @Test
    public void testInsertOrUpdate_InsertsAndReturnsCurrency_WhenNotFoundInRepository() {
        Currency currency = new Currency();
        currency.setCurrencyCode("EUR");
        currency.setUpdateTime(LocalDateTime.now());

        when(entityTemplate.selectOne(any(Query.class), eq(Currency.class))).thenReturn(Mono.empty());
        when(entityTemplate.insert(currency)).thenReturn(Mono.just(currency));

        Mono<Currency> result = currencyRepository.insertOrUpdate(currency);

        assertNotNull(result);
        assertEquals(currency, result.block());
        verify(entityTemplate, never()).update(any(Currency.class));
        verify(entityTemplate, times(1)).insert(currency);
    }
}