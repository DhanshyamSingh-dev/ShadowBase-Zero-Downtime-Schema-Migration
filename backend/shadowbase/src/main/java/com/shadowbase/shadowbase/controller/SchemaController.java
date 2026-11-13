
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

    @GetMapping("/api/schema/tables")
    public List<String> getTables() {
        try {
            return schemaReaderService.getTableNames();
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unable to read database schema."
            );
        }
    }

    @PostMapping("/api/schema/compare")
    public Map<String, Object> compareSchema(
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
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Schema comparison failed."
            );
        }
    }
}
