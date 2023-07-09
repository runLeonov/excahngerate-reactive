package com.example.demo.repository;

import com.example.demo.entity.Currency;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface CurrencyRepository extends ReactiveCrudRepository<Currency, String> {
    Mono<Currency> findByCurrencyCode(String currencyCode);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE currency_rate")
    void truncateTable();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO currency_rate (currency_code, exchange_rate, update_time) " +
            "VALUES (:#{#currency.currencyCode}, :#{#currency.exchangeRate}, :#{#currency.updateTime})")
    Mono<Currency> insertCurrency(@Param("currency") Currency currency);
}