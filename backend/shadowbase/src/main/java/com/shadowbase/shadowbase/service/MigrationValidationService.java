
package com.shadowbase.shadowbase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class MigrationValidationService {

    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
        "^CREATE TABLE IF NOT EXISTS `[A-Za-z][A-Za-z0-9_]*` "
        + "\\(id BIGINT PRIMARY KEY AUTO_INCREMENT\\);$"
    );

    private static final Pattern ALTER_COLUMN_PATTERN = Pattern.compile(
        "^ALTER TABLE `[A-Za-z][A-Za-z0-9_]*` "
        + "(ADD COLUMN|MODIFY COLUMN) "
        + "`[A-Za-z][A-Za-z0-9_]*` "
        + "(VARCHAR|CHAR|TEXT|TINYTEXT|MEDIUMTEXT|LONGTEXT|"
        + "BIGINT|INT|INTEGER|SMALLINT|TINYINT|DECIMAL|NUMERIC|"
        + "FLOAT|DOUBLE|DATE|DATETIME|TIMESTAMP|TIME|BOOLEAN|"
        + "BIT|BINARY|VARBINARY|BLOB)"
        + "(\\([0-9]{1,5}\\))? "
        + "(NULL|NOT NULL);$"
    );

    public List<String> validatePlan(List<String> migrationPlan) {

        List<String> errors = new ArrayList<>();

        if (migrationPlan == null) {
            errors.add("Migration plan is missing.");
            return errors;
        }

        // An empty plan means no migration is required.
        if (migrationPlan.isEmpty()) {
            return errors;
        }

        for (String statement : migrationPlan) {

            if (statement == null || statement.isBlank()) {
                errors.add("Empty migration statement detected.");
                continue;
            }

            if (statement.startsWith("MANUAL REVIEW REQUIRED:")) {
                errors.add(statement);
                continue;
            }

            if (CREATE_TABLE_PATTERN.matcher(statement).matches()) {
                continue;
            }

            if (ALTER_COLUMN_PATTERN.matcher(statement).matches()) {
                continue;
            }

            errors.add(
                "Unsupported or unsafe migration statement: " + statement
            );
        }

        return errors;
    }
}
