package com.example.demo.config;

import com.example.demo.CurrencyEnum;
import com.example.demo.dto.ExchangeRateDTO;
import com.example.demo.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
public class CronJobsConfig {
    private final WebClient webClient;
    private final CurrencyService service;

    @Autowired
    public CronJobsConfig(WebClient webClient, CurrencyService service) {
        this.webClient = webClient;
        this.service = service;
    }
    @Scheduled(fixedRate = 1000000L)
    public void scheduleCurrencyRateUpdates() {
        log.info("Reaching API to get ExchangeRates...");
        for (CurrencyEnum el: CurrencyEnum.values()) {
            Mono<ExchangeRateDTO> exchangeRateDto = webClient.get()
                    .uri("/latest/" + el)
                    .retrieve()
                    .bodyToMono(ExchangeRateDTO.class)
                    .subscribeOn(Schedulers.boundedElastic());
            service.convertToCurrencyRateFlux(exchangeRateDto).flatMap(service::reinsert).subscribe();
            log.info("Got it for " + el);
        }
    }
}
