package com.yuramoroz.spring_crm_system.healthIndicators;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {

    private final File path;

    private final long threshold;

    // Default constructor: checks the current working directory with a 1GB threshold.
    public DiskSpaceHealthIndicator() {
        this(new File("."), 1073741824L);  // Default to 1GB threshold (in 1 GB there are 1073741824 bytes)
    }

    public DiskSpaceHealthIndicator(File path, long threshold) {
        this.path = path;
        this.threshold = threshold;
    }

    @Override
    public Health health() {
        // Retrieve disk space metrics in bytes.
        long totalSpace = path.getTotalSpace();
        long freeSpace = path.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        // Convert values to gigabytes.
        double totalGB = bytesToGigabytes(totalSpace);
        double freeGB = bytesToGigabytes(freeSpace);
        double usedGB = bytesToGigabytes(usedSpace);

        Health.Builder builder = (freeSpace >= threshold) ? Health.up() : Health.down();

        builder.withDetail("totalSpaceGB", round(totalGB, 2));
        builder.withDetail("freeSpaceGB", round(freeGB, 2));
        builder.withDetail("usedSpaceGB", round(usedGB, 2));
        builder.withDetail("thresholdGB", round(bytesToGigabytes(threshold), 2));

        return builder.build();
    }

    private double bytesToGigabytes(long bytes) {
        return bytes / (1024.0 * 1024.0 * 1024.0);
    }

    /**
     * Rounds a double value to the specified number of decimal places.
     *
     * @param value  the value to round.
     * @param places the number of decimal places.
     * @return the rounded value.
     */
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Decimal places must be non-negative");
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
