package com.yuramoroz.spring_crm_system.metrics;

import com.yuramoroz.spring_crm_system.service.TrainingService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainingsMetrics {

    public TrainingsMetrics(TrainingService trainingService, MeterRegistry meterRegistry) {

        Gauge.builder("training.count", trainingService, TrainingService::getAllTrainingsCount)
                .description("The number of trainings in the DB")
                .register(meterRegistry);

    }
}
