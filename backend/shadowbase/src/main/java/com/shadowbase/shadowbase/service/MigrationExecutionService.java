
package com.shadowbase.shadowbase.service;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.shadowbase.shadowbase.schema.MigrationExecutionResult;

@Service
public class MigrationExecutionService {

    private final DataSource migrationDataSource;
    private final MigrationValidationService validationService;

    public MigrationExecutionService(
            @Qualifier("migrationDataSource")
            DataSource migrationDataSource,
            MigrationValidationService validationService) {

        this.migrationDataSource = migrationDataSource;
        this.validationService = validationService;
    }

    public MigrationExecutionResult executeMigration(
            List<String> approvedSql) {

        MigrationExecutionResult result =
                new MigrationExecutionResult();

        result.setStartTime(LocalDateTime.now());

        List<String> executedStatements = new ArrayList<>();
        result.setExecutedStatements(executedStatements);

        try {

            if (approvedSql == null || approvedSql.isEmpty()) {
                throw new IllegalArgumentException(
                        "No approved SQL statements provided."
                );
            }

            // Validate SQL before execution
            List<String> validationErrors =
                    validationService.validatePlan(approvedSql);

            if (!validationErrors.isEmpty()) {
                throw new IllegalArgumentException(
                        String.join("; ", validationErrors)
                );
            }

            try (Connection connection =
                         migrationDataSource.getConnection()) {

                // Safety check
                if (!"shadowbase_migration_test".equals(
                        connection.getCatalog())) {

                    throw new IllegalStateException(
                            "Migration blocked: unsafe database target."
                    );
                }

                try (Statement statement =
                             connection.createStatement()) {

                    for (String sql : approvedSql) {

                        statement.executeUpdate(sql);
                        executedStatements.add(sql);
                    }
                }
            }

            result.setStatus("SUCCESS");
            result.setErrorMessage(null);

        } catch (Exception e) {

            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());

        } finally {

            result.setEndTime(LocalDateTime.now());
        }

        return result;
    }
}
