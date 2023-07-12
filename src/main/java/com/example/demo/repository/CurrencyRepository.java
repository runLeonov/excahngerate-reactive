package com.example.demo.repository;

import com.example.demo.entity.Currency;
import com.example.demo.entity.CurrencyRate;
import com.example.demo.entity.ExchangeRate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrencyRepository extends ReactiveMongoRepository<Currency, String> {
    Mono<Currency> findByBasicCode(String currencyCode);
    @Query(value = "{ '_id': ?0 }", fields =
            "{ 'currenciesRates': { $elemMatch: { 'currencyExchangeCode': ?1 } } }")
    Mono<Currency> findCurrencyRateByIdAndCurrencyCode(String id, String currencyCode);
}