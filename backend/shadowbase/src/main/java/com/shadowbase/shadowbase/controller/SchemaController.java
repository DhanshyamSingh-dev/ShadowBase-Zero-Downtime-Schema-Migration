
package com.shadowbase.shadowbase.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shadowbase.shadowbase.schema.SchemaDefinition;
import com.shadowbase.shadowbase.schema.SchemaDifference;
import com.shadowbase.shadowbase.schema.TargetSchemaDTO;
import com.shadowbase.shadowbase.schema.TableInfo;
import com.shadowbase.shadowbase.service.SchemaReaderService;
import com.shadowbase.shadowbase.service.SchemaComparisonService;
import com.shadowbase.shadowbase.service.MigrationPlanService;
import com.shadowbase.shadowbase.service.MigrationValidationService;

@RestController
public class SchemaController {

    private final SchemaReaderService schemaReaderService;
    private final SchemaComparisonService comparisonService;
    private final MigrationPlanService planService;
    private final MigrationValidationService validationService;

    public SchemaController(
            SchemaReaderService schemaReaderService,
            SchemaComparisonService comparisonService,
            MigrationPlanService planService,
            MigrationValidationService validationService) {

        this.schemaReaderService = schemaReaderService;
        this.comparisonService = comparisonService;
        this.planService = planService;
        this.validationService = validationService;
    }

    // Get tables with column details
    @GetMapping("/api/schema/tables")
    public List<TableInfo> getTables() {

        try {
            return schemaReaderService.getTables();

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to read database schema."
            );
        }
    }

    // Existing table-level comparison endpoint
    @PostMapping("/api/schema/compare/tables")
    public Map<String, Object> compareTables(
            @RequestBody SchemaDefinition schema) {

        try {

            List<String> currentTables =
                    schemaReaderService.getTableNames();

            schema.setCurrentTables(currentTables);

            List<String> changes =
                    comparisonService.compareSchemas(schema);

            List<String> migrationPlan =
                    planService.generatePlan(changes);

            List<String> errors =
                    validationService.validatePlan(migrationPlan);

            Map<String, Object> response = new HashMap<>();

            response.put("currentTables", currentTables);
            response.put("targetTables", schema.getTargetTables());
            response.put("changes", changes);
            response.put("migrationPlan", migrationPlan);
            response.put("validationErrors", errors);
            response.put("valid", errors.isEmpty());

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Full schema comparison failed.",
                    e
            );
        }
    }

    // New full schema comparison endpoint

@PostMapping("/api/schema/compare")
public Map<String, Object> compareFullSchema(
        @RequestBody TargetSchemaDTO targetSchema) {

    try {
        List<TableInfo> currentSchema =
                schemaReaderService.getTables();

        List<SchemaDifference> differences =
                comparisonService.compareSchemas(
                        currentSchema,
                        targetSchema
                );

        List<String> migrationPlan =
                planService.generatePlan(
                        differences,
                        targetSchema
                );

        List<String> validationErrors =
                validationService.validatePlan(migrationPlan);

        Map<String, Object> response = new HashMap<>();

        response.put("currentSchema", currentSchema);
        response.put("targetSchema", targetSchema);
        response.put("differences", differences);
        response.put("migrationPlan", migrationPlan);
        response.put("validationErrors", validationErrors);

        return response;

    } catch (Exception e) {
        e.printStackTrace();

        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Full schema comparison and migration planning failed.",
                e
        );
    }
}
@PostMapping("/api/migrations/plan")
public Map<String, Object> generateMigrationPlan(
        @RequestBody TargetSchemaDTO targetSchema) {

    try {
        List<TableInfo> currentSchema =
                schemaReaderService.getTables();

        List<SchemaDifference> differences =
                comparisonService.compareSchemas(
                        currentSchema,
                        targetSchema
                );

        List<String> migrationPlan =
                planService.generatePlan(
                        differences,
                        targetSchema
                );

        List<String> validationErrors =
                validationService.validatePlan(migrationPlan);

        Map<String, Object> response = new HashMap<>();

        response.put("differences", differences);
        response.put("migrationPlan", migrationPlan);
        response.put("validationErrors", validationErrors);

        return response;

    } catch (Exception e) {
        e.printStackTrace();

        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Migration plan generation failed.",
                e
        );
    }
}

}
