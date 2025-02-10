package com.yuramoroz.spring_crm_system.metrics;

import com.yuramoroz.spring_crm_system.service.TrainerService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainerMetrics {

    public TrainerMetrics(TrainerService trainerService, MeterRegistry meterRegistry) {

        Gauge.builder("trainer.count", trainerService, TrainerService::getAllUsersCount)
                .description("The number of trainers in the DB")
                .register(meterRegistry);

    }
}