
package com.shadowbase.shadowbase.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.shadowbase.shadowbase.service.MigrationDatabaseService;

@RestController
public class MigrationDatabaseController {

    private final MigrationDatabaseService migrationDatabaseService;

    public MigrationDatabaseController(
            MigrationDatabaseService migrationDatabaseService) {

        this.migrationDatabaseService = migrationDatabaseService;
    }

    @GetMapping("/api/migrations/database")
    public Map<String, String> getMigrationDatabase() {

        try {
            String databaseName =
                    migrationDatabaseService.getDatabaseName();

            Map<String, String> response = new HashMap<>();

            response.put("databaseName", databaseName);
            response.put("status", "CONNECTED");

            return response;

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to connect to migration database.",
                    e
            );
        }
    }
}
