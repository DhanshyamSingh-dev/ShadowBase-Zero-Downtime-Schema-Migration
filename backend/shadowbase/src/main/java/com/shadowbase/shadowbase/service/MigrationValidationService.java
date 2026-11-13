
package com.shadowbase.shadowbase.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MigrationValidationService {

    public List<String> validatePlan(List<String> migrationPlan) {

        List<String> errors = new ArrayList<>();

        if (migrationPlan == null || migrationPlan.isEmpty()) {
            errors.add("Migration plan is empty.");
            return errors;
        }

        for (String statement : migrationPlan) {

            if (statement == null || statement.isBlank()) {
                errors.add("Empty migration statement detected.");
                continue;
            }

            if (statement.startsWith("CREATE TABLE IF NOT EXISTS `")
                    && statement.endsWith(";")) {
                continue;
            }

            if (statement.startsWith("MANUAL REVIEW REQUIRED:")) {
                errors.add(statement);
                continue;
            }

            errors.add("Unsupported or unsafe migration statement.");
        }

        return errors;
    }
}
