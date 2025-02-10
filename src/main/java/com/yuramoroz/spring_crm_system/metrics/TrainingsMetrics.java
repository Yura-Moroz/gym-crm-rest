package com.yuramoroz.spring_crm_system.metrics;

import com.yuramoroz.spring_crm_system.service.TrainingService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingsMetrics {

    private final TrainingService trainingService;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    private void init() {
        Gauge.builder("training.count", trainingService, TrainingService::count)
                .description("The number of trainings in the DB")
                .register(meterRegistry);
    }
}
