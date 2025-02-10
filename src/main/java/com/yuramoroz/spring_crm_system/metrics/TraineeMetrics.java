package com.yuramoroz.spring_crm_system.metrics;

import com.yuramoroz.spring_crm_system.service.TraineeService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineeMetrics {

    private final TraineeService traineeService;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void init() {
        Gauge.builder("trainee.count", traineeService, TraineeService::count)
                .description("The number of trainees in the DB")
                .register(meterRegistry);
    }
}
