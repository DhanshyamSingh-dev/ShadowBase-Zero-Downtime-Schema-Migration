
package com.shadowbase.shadowbase.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MigrationPlanService {

    public List<String> generatePlan(List<String> changes) {

        List<String> migrationPlan = new ArrayList<>();

        for (String change : changes) {

            if (change.startsWith("ADD TABLE: ")) {

                String tableName =
                        change.substring("ADD TABLE: ".length()).trim();

                if (tableName.matches("[A-Za-z][A-Za-z0-9_]*")) {
                    migrationPlan.add(
                        "CREATE TABLE IF NOT EXISTS `" + tableName +
                        "` (id BIGINT PRIMARY KEY AUTO_INCREMENT);"
                    );
                }
            }

            else if (change.startsWith("REMOVE TABLE: ")) {

                String tableName =
                        change.substring("REMOVE TABLE: ".length()).trim();

                migrationPlan.add(
                    "MANUAL REVIEW REQUIRED: Table removal - " + tableName
                );
            }
        }

        return migrationPlan;
    }
}
