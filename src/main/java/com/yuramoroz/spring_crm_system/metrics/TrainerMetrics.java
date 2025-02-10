package com.yuramoroz.spring_crm_system.metrics;

import com.yuramoroz.spring_crm_system.service.TrainerService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerMetrics {

    private final TrainerService trainerService;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    private void init() {
        Gauge.builder("trainer.count", trainerService, TrainerService::count)
                .description("The number of trainers in the DB")
                .register(meterRegistry);
    }
}