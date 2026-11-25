
package com.shadowbase.shadowbase.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.shadowbase.shadowbase.schema.ColumnInfo;
import com.shadowbase.shadowbase.schema.SchemaDifference;
import com.shadowbase.shadowbase.schema.TargetSchemaDTO;
import com.shadowbase.shadowbase.schema.TableInfo;

@Service
public class MigrationPlanService {

    // Existing table-level migration method
    public List<String> generatePlan(List<String> changes) {

        List<String> migrationPlan = new ArrayList<>();

        for (String change : changes) {

            if (change.startsWith("ADD TABLE: ")) {

                String tableName =
                        change.substring("ADD TABLE: ".length()).trim();

                if (isSafeIdentifier(tableName)) {
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

    // New structured migration plan method
    public List<String> generatePlan(
            List<SchemaDifference> differences,
            TargetSchemaDTO targetSchema) {

        List<String> migrationPlan = new ArrayList<>();
        Set<String> modifiedColumns = new HashSet<>();

        for (SchemaDifference difference : differences) {

            String changeType = difference.getChangeType();
            String tableName = difference.getTableName();
            String columnName = difference.getColumnName();

            if ("ADD_COLUMN".equals(changeType)) {

                ColumnInfo targetColumn =
                        findTargetColumn(targetSchema, tableName, columnName);

                if (targetColumn == null) {
                    migrationPlan.add(
                        "MANUAL REVIEW REQUIRED: Target column details missing - "
                        + tableName + "." + columnName
                    );
                    continue;
                }

                String definition = buildColumnDefinition(targetColumn);

                if (definition == null || !isSafeIdentifier(tableName)
                        || !isSafeIdentifier(columnName)) {
                    migrationPlan.add(
                        "MANUAL REVIEW REQUIRED: Unsafe column definition - "
                        + tableName + "." + columnName
                    );
                    continue;
                }

                migrationPlan.add(
                    "ALTER TABLE `" + tableName + "` ADD COLUMN `"
                    + columnName + "` " + definition + ";"
                );
            }

            else if ("MODIFY_DATA_TYPE".equals(changeType)
                    || "MODIFY_NULLABLE".equals(changeType)) {

                String columnKey = tableName + "." + columnName;

                if (!modifiedColumns.add(columnKey)) {
                    continue;
                }

                ColumnInfo targetColumn =
                        findTargetColumn(targetSchema, tableName, columnName);

                if (targetColumn == null) {
                    migrationPlan.add(
                        "MANUAL REVIEW REQUIRED: Target column details missing - "
                        + columnKey
                    );
                    continue;
                }

                String definition = buildColumnDefinition(targetColumn);

                if (definition == null || !isSafeIdentifier(tableName)
                        || !isSafeIdentifier(columnName)) {
                    migrationPlan.add(
                        "MANUAL REVIEW REQUIRED: Unsafe column modification - "
                        + columnKey
                    );
                    continue;
                }

                migrationPlan.add(
                    "ALTER TABLE `" + tableName + "` MODIFY COLUMN `"
                    + columnName + "` " + definition + ";"
                );
            }

            else if ("REMOVE_COLUMN".equals(changeType)) {

                migrationPlan.add(
                    "MANUAL REVIEW REQUIRED: Column removal - "
                    + tableName + "." + columnName
                );
            }

            else if ("REMOVE_TABLE".equals(changeType)) {

                migrationPlan.add(
                    "MANUAL REVIEW REQUIRED: Table removal - " + tableName
                );
            }

            else if ("ADD_TABLE".equals(changeType)) {

                migrationPlan.add(
                    "MANUAL REVIEW REQUIRED: New table creation - " + tableName
                );
            }

            else if ("MODIFY_DEFAULT".equals(changeType)) {

                migrationPlan.add(
                    "MANUAL REVIEW REQUIRED: Default value modification - "
                    + tableName + "." + columnName
                );
            }

            else {

                migrationPlan.add(
                    "MANUAL REVIEW REQUIRED: Unsupported change - "
                    + changeType
                );
            }
        }

        return migrationPlan;
    }

    // Find a column in the target schema
    private ColumnInfo findTargetColumn(
            TargetSchemaDTO targetSchema,
            String tableName,
            String columnName) {

        if (targetSchema == null || targetSchema.getTables() == null) {
            return null;
        }

        for (TableInfo table : targetSchema.getTables()) {

            if (table.getTableName().equalsIgnoreCase(tableName)) {

                if (table.getColumns() == null) {
                    return null;
                }

                for (ColumnInfo column : table.getColumns()) {

                    if (column.getColumnName().equalsIgnoreCase(columnName)) {
                        return column;
                    }
                }
            }
        }

        return null;
    }

    // Build a safe MySQL column definition
    private String buildColumnDefinition(ColumnInfo column) {

        String dataType = column.getDataType().toUpperCase();

        if (!isSupportedDataType(dataType)) {
            return null;
        }

        String definition = dataType;

        if (column.getLength() != null) {

            if (column.getLength() <= 0 || column.getLength() > 65535) {
                return null;
            }

            if (dataType.equals("VARCHAR")
                    || dataType.equals("CHAR")
                    || dataType.equals("VARBINARY")
                    || dataType.equals("BINARY")
                    || dataType.equals("DECIMAL")
                    || dataType.equals("NUMERIC")) {

                definition += "(" + column.getLength() + ")";
            }
        }

        definition += column.isNullable() ? " NULL" : " NOT NULL";

        return definition;
    }

    // Allow only supported MySQL data types
    private boolean isSupportedDataType(String dataType) {

        return Set.of(
            "VARCHAR", "CHAR", "TEXT",
            "TINYTEXT", "MEDIUMTEXT", "LONGTEXT",
            "BIGINT", "INT", "INTEGER", "SMALLINT", "TINYINT",
            "DECIMAL", "NUMERIC", "FLOAT", "DOUBLE",
            "DATE", "DATETIME", "TIMESTAMP", "TIME",
            "BOOLEAN", "BIT",
            "BINARY", "VARBINARY", "BLOB"
        ).contains(dataType);
    }

    // Validate SQL identifiers
    private boolean isSafeIdentifier(String identifier) {

        return identifier != null
                && identifier.matches("[A-Za-z][A-Za-z0-9_]*");
    }
}
