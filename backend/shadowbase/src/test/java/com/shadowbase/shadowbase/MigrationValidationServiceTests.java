
package com.shadowbase.shadowbase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.shadowbase.shadowbase.service.MigrationValidationService;

class MigrationValidationServiceTests {

    @Test
    void rejectsUnsupportedSql() {

        MigrationValidationService validationService =
                new MigrationValidationService();

        List<String> migrationPlan = List.of(
                "DROP TABLE `products`;"
        );

        List<String> errors =
                validationService.validatePlan(migrationPlan);

        assertTrue(errors.contains(
                "Unsupported or unsafe migration statement: DROP TABLE `products`;"
        ));
    }
}
