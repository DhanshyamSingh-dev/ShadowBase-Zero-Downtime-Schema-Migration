
package com.shadowbase.shadowbase.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shadowbase.shadowbase.schema.SchemaDefinition;
import com.shadowbase.shadowbase.schema.SchemaDifference;
import com.shadowbase.shadowbase.schema.TargetSchemaDTO;
import com.shadowbase.shadowbase.schema.TableInfo;
import com.shadowbase.shadowbase.schema.ColumnInfo;

@Service
public class SchemaComparisonService {

    // Existing table-level comparison method
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

    // New structured schema comparison method
    public List<SchemaDifference> compareSchemas(
            List<TableInfo> currentTables,
            TargetSchemaDTO targetSchema) {

        System.out.println("========== SHADOWBASE COMPARISON STARTED ==========");
        System.out.println("Current tables: " + currentTables.size());
        System.out.println("Target schema received: " + (targetSchema != null));

        List<SchemaDifference> differences = new ArrayList<>();

        List<TableInfo> targetTables = targetSchema.getTables();

        System.out.println("Target tables: " + targetTables.size());

        // Compare target tables with current tables
        for (TableInfo targetTable : targetTables) {

            TableInfo currentTable = findTable(
                    currentTables, targetTable.getTableName());

            if (currentTable == null) {

                differences.add(new SchemaDifference(
                        "ADD_TABLE",
                        targetTable.getTableName(),
                        null,
                        null,
                        "Table does not exist"
                ));

                continue;
            }

            // Compare columns
            for (ColumnInfo targetColumn : targetTable.getColumns()) {

                ColumnInfo currentColumn = findColumn(
                        currentTable.getColumns(),
                        targetColumn.getColumnName());

                if (currentColumn == null) {

                    differences.add(new SchemaDifference(
                            "ADD_COLUMN",
                            targetTable.getTableName(),
                            targetColumn.getColumnName(),
                            null,
                            formatColumn(targetColumn)
                    ));

                    continue;
                }

                // Compare data type and length
                String currentType = formatDataType(currentColumn);
                String targetType = formatDataType(targetColumn);

                if (!currentType.equalsIgnoreCase(targetType)) {

                    differences.add(new SchemaDifference(
                            "MODIFY_DATA_TYPE",
                            targetTable.getTableName(),
                            targetColumn.getColumnName(),
                            currentType,
                            targetType
                    ));
                }

                // Compare nullable property
                if (currentColumn.isNullable()
                        != targetColumn.isNullable()) {

                    differences.add(new SchemaDifference(
                            "MODIFY_NULLABLE",
                            targetTable.getTableName(),
                            targetColumn.getColumnName(),
                            String.valueOf(currentColumn.isNullable()),
                            String.valueOf(targetColumn.isNullable())
                    ));
                }

                // Compare default value
                if (!java.util.Objects.equals(
                        currentColumn.getDefaultValue(),
                        targetColumn.getDefaultValue())) {

                    differences.add(new SchemaDifference(
                            "MODIFY_DEFAULT",
                            targetTable.getTableName(),
                            targetColumn.getColumnName(),
                            String.valueOf(currentColumn.getDefaultValue()),
                            String.valueOf(targetColumn.getDefaultValue())
                    ));
                }
            }

            // Detect removed columns
            for (ColumnInfo currentColumn : currentTable.getColumns()) {

                if (findColumn(
                        targetTable.getColumns(),
                        currentColumn.getColumnName()) == null) {

                    differences.add(new SchemaDifference(
                            "REMOVE_COLUMN",
                            targetTable.getTableName(),
                            currentColumn.getColumnName(),
                            formatColumn(currentColumn),
                            null
                    ));
                }
            }
        }

        // Detect removed tables
        for (TableInfo currentTable : currentTables) {

            if (findTable(
                    targetTables, currentTable.getTableName()) == null) {

                differences.add(new SchemaDifference(
                        "REMOVE_TABLE",
                        currentTable.getTableName(),
                        null,
                        "Table exists",
                        null
                ));
            }
        }

        System.out.println("Total differences found: " + differences.size());
        System.out.println("========== SHADOWBASE COMPARISON FINISHED ==========");

        return differences;
    }

    private TableInfo findTable(
            List<TableInfo> tables, String tableName) {

        for (TableInfo table : tables) {
            if (table.getTableName().equalsIgnoreCase(tableName)) {
                return table;
            }
        }

        return null;
    }

    private ColumnInfo findColumn(
            List<ColumnInfo> columns, String columnName) {

        for (ColumnInfo column : columns) {
            if (column.getColumnName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }

        return null;
    }

    private String formatDataType(ColumnInfo column) {

        if (column.getLength() != null) {
            return column.getDataType() + "("
                    + column.getLength() + ")";
        }

        return column.getDataType();
    }

    private String formatColumn(ColumnInfo column) {

        return formatDataType(column)
                + ", nullable=" + column.isNullable()
                + ", default=" + column.getDefaultValue();
    }
}
