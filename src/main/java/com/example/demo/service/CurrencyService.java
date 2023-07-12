package com.example.demo.service;

import com.example.demo.dto.ExchangeRateDTO;
import com.example.demo.entity.Currency;
import com.example.demo.entity.CurrencyRate;
import com.example.demo.entity.ExchangeRate;
import com.example.demo.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRateRepo) {
        this.currencyRepository = currencyRateRepo;
    }

    public Mono<Currency> getCurrencyRate(String currencyCode) {
        return currencyRepository.findByBasicCode(currencyCode);
    }

    public Mono<Currency> getCurrencyRateForCurrency(String currencyCode, String currencyCodeFrom) {
        return currencyRepository.findCurrencyRateByIdAndCurrencyCode(currencyCode, currencyCodeFrom);
    }

    public Flux<Currency> getCurrenciesRates() {
        return currencyRepository.findAll();
    }

    @Transactional
    public Mono<Currency> reinsert(Currency currency) {
        Mono<Currency> existingCurrencyCodeMono = currencyRepository.findById(currency.getBasicCode());
        return existingCurrencyCodeMono.flatMap(existingCurrencyCode -> {
            List<CurrencyRate> existingRates = existingCurrencyCode.getCurrenciesRates();
            List<CurrencyRate> newRates = currency.getCurrenciesRates();
            int counter = 0;

            for (CurrencyRate rate : existingRates) {
                List<ExchangeRate> existingRate = rate.getExchangeRates();
                if (Objects.nonNull(existingRate) &&
                        !existingRate.contains(newRates.get(counter).getLastExchangeRate())) {
                    existingRate.add(newRates.get(counter).getLastExchangeRate());
                    rate.setExchangeRates(existingRate);
                }
                counter++;
            }
            existingCurrencyCode.setCurrenciesRates(existingRates);
            return currencyRepository.save(existingCurrencyCode);
        }).switchIfEmpty(Mono.defer(() -> {
            return currencyRepository.save(currency);
        }));
    }


    public Flux<Currency> convertToCurrencyRateFlux(Mono<ExchangeRateDTO> exchangeRateDtoMono) {
        return exchangeRateDtoMono.flatMapMany(exchange -> {
            return Flux.just(ExchangeRateDTO.mapToCurrency(exchange));
        });
    }
}