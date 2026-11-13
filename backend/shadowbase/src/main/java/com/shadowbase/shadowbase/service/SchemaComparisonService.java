
package com.shadowbase.shadowbase.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shadowbase.shadowbase.schema.SchemaDefinition;

@Service
public class SchemaComparisonService {

    public List<String> compareSchemas(SchemaDefinition schema) {

        List<String> changes = new ArrayList<>();

        List<String> currentTables = schema.getCurrentTables();
        List<String> targetTables = schema.getTargetTables();

        for (String table : targetTables) {
            if (!currentTables.contains(table)) {
                changes.add("ADD TABLE: " + table);
            }
        }

        for (String table : currentTables) {
            if (!targetTables.contains(table)) {
                changes.add("REMOVE TABLE: " + table);
            }
        }

        return changes;
    }
}
