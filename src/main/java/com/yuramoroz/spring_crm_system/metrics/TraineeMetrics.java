package com.yuramoroz.spring_crm_system.metrics;

import com.yuramoroz.spring_crm_system.service.TraineeService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TraineeMetrics {

    public TraineeMetrics(TraineeService traineeService, MeterRegistry meterRegistry) {

        Gauge.builder("trainee.count", traineeService, TraineeService::getAllUsersCount)
                .description("The number of trainees in the DB")
                .register(meterRegistry);

    }
}
