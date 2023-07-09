package com.example.demo.repository;

import com.example.demo.entity.Currency;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class CurrencyRepository {
    private final R2dbcEntityTemplate entityTemplate;

    public CurrencyRepository(R2dbcEntityTemplate entityTemplate) {
        this.entityTemplate = entityTemplate;
    }

    public Mono<Currency> findByCode(String currencyCode) {
        return entityTemplate.selectOne(Query.query(Criteria.where("currency_code").is(currencyCode)), Currency.class);
    }

    public Flux<Currency> findAll() {
        return entityTemplate.select(Query.empty(), Currency.class);
    }

    public Mono<Currency> insertOrUpdate(Currency currency) {
        String currencyCode = currency.getCurrencyCode();
        LocalDateTime updateTime = currency.getUpdateTime();

        return entityTemplate.selectOne(Query.query(Criteria.where("currency_code").is(currencyCode)), Currency.class)
                .flatMap(existingCurrency -> {
                    LocalDateTime existingUpdateTime = existingCurrency.getUpdateTime();
                    if (existingUpdateTime.isEqual(updateTime)) {
                        return Mono.just(existingCurrency);
                    } else {
                        existingCurrency.setUpdateTime(updateTime);
                        return entityTemplate.update(existingCurrency).thenReturn(existingCurrency);
                    }
                })
                .switchIfEmpty(entityTemplate.insert(currency));
    }
}