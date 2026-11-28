
package com.shadowbase.shadowbase.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shadowbase.shadowbase.schema.MigrationExecutionResult;
import com.shadowbase.shadowbase.schema.SchemaDifference;
import com.shadowbase.shadowbase.schema.TargetSchemaDTO;
import com.shadowbase.shadowbase.schema.TableInfo;

@Service
public class MigrationSimulationService {

    private final MigrationSchemaReaderService schemaReaderService;
    private final SchemaComparisonService comparisonService;
    private final MigrationPlanService planService;
    private final MigrationValidationService validationService;
    private final MigrationExecutionService executionService;

    public MigrationSimulationService(
            MigrationSchemaReaderService schemaReaderService,
            SchemaComparisonService comparisonService,
            MigrationPlanService planService,
            MigrationValidationService validationService,
            MigrationExecutionService executionService) {

        this.schemaReaderService = schemaReaderService;
        this.comparisonService = comparisonService;
        this.planService = planService;
        this.validationService = validationService;
        this.executionService = executionService;
    }

    public MigrationExecutionResult simulateMigration(
            TargetSchemaDTO targetSchema) {

        LocalDateTime startTime = LocalDateTime.now();

        try {

            if (targetSchema == null || targetSchema.getTables() == null) {
                throw new IllegalArgumentException(
                        "Target schema is missing."
                );
            }

            // Step 1: Read current migration database schema
            List<TableInfo> currentSchema =
                    schemaReaderService.getTables();

            // Step 2: Compare current schema with target schema
            List<SchemaDifference> differences =
                    comparisonService.compareSchemas(
                            currentSchema, targetSchema
                    );

            // Step 3: Generate migration SQL plan
            List<String> migrationPlan =
                    planService.generatePlan(
                            differences, targetSchema
                    );

            // Step 4: Validate generated SQL
            List<String> validationErrors =
                    validationService.validatePlan(migrationPlan);

            if (!validationErrors.isEmpty()) {

                return createFailedResult(
                        startTime,
                        String.join("; ", validationErrors)
                );
            }

            // No changes required
            if (migrationPlan.isEmpty()) {

                MigrationExecutionResult result =
                        new MigrationExecutionResult();

                result.setStartTime(startTime);
                result.setEndTime(LocalDateTime.now());
                result.setStatus("SUCCESS");
                result.setExecutedStatements(new ArrayList<>());
                result.setSchemaVerified(true);
                result.setVerificationMessage(
                        "No migration required. Schema already matches target."
                );

                return result;
            }

            // Step 5: Execute approved SQL
            MigrationExecutionResult result =
                    executionService.executeMigration(migrationPlan);

            // Step 6: Verify schema after execution
            List<TableInfo> updatedSchema =
                    schemaReaderService.getTables();

            List<SchemaDifference> remainingDifferences =
                    comparisonService.compareSchemas(
                            updatedSchema, targetSchema
                    );

            boolean verified = remainingDifferences.isEmpty();

            result.setSchemaVerified(verified);

            if (verified) {
                result.setVerificationMessage(
                        "Migration schema matches the requested target schema."
                );
            } else {
                result.setVerificationMessage(
                        "Schema verification failed. "
                        + remainingDifferences.size()
                        + " differences remain."
                );
            }

            return result;

        } catch (Exception e) {

            return createFailedResult(startTime, e.getMessage());
        }
    }

    private MigrationExecutionResult createFailedResult(
            LocalDateTime startTime, String errorMessage) {

        MigrationExecutionResult result =
                new MigrationExecutionResult();

        result.setStartTime(startTime);
        result.setEndTime(LocalDateTime.now());
        result.setStatus("FAILED");
        result.setErrorMessage(errorMessage);
        result.setExecutedStatements(new ArrayList<>());
        result.setSchemaVerified(false);
        result.setVerificationMessage(
                "Migration could not be completed."
        );

        return result;
    }
}
