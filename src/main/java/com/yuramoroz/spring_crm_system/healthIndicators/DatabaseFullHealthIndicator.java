package com.yuramoroz.spring_crm_system.healthIndicators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DatabaseFullHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        Map<String, Object> tableCounts = new HashMap<>();
        boolean allTablesEmpty = true;

        try (Connection connection = dataSource.getConnection()) {
            // Get the current catalog (database name in MySQL)
            String catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();

            // Retrieve all tables in the current catalog
            try (ResultSet tables = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    int count = countRows(connection, tableName);
                    tableCounts.put(tableName, count);

                    if (count > 0) {
                        allTablesEmpty = false;
                    }
                }
            }

            // Build health based on whether all tables are empty or not.
            if (allTablesEmpty) {
                return Health.down()
                        .withDetail("tables", tableCounts)
                        .withDetail("message", "Database is empty")
                        .build();
            } else {
                return Health.up()
                        .withDetail("tables", tableCounts)
                        .withDetail("message", "Database contains data")
                        .build();
            }
        } catch (SQLException ex) {
            return Health.down(ex).build();
        }
    }

    /**
     * Helper method to count the number of rows in a given table.
     *
     * @param connection the active database connection
     * @param tableName  the name of the table
     * @return the number of rows in the table
     * @throws SQLException if an SQL error occurs
     */
    private int countRows(Connection connection, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
