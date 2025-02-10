package com.yuramoroz.spring_crm_system.healthIndicators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class CustomDatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        try(Connection connection = dataSource.getConnection()){
            return Health.up().withDetail("database", "Connected").build();
        }catch (Exception e){
            return Health.down().withDetail("database", "Not reachable").build();
        }
    }
}
