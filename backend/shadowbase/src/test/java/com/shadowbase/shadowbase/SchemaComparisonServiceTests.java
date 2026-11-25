
package com.shadowbase.shadowbase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.shadowbase.shadowbase.schema.ColumnInfo;
import com.shadowbase.shadowbase.schema.SchemaDifference;
import com.shadowbase.shadowbase.schema.TargetSchemaDTO;
import com.shadowbase.shadowbase.schema.TableInfo;
import com.shadowbase.shadowbase.service.SchemaComparisonService;

class SchemaComparisonServiceTests {

    private final SchemaComparisonService comparisonService =
            new SchemaComparisonService();

    @Test
    void detectsNoSchemaChanges() {

        List<ColumnInfo> columns = List.of(
                new ColumnInfo("id", "BIGINT", false, 19, null),
                new ColumnInfo("name", "VARCHAR", true, 100, null)
        );

        List<TableInfo> currentTables = List.of(
                new TableInfo("users", columns)
        );

        TargetSchemaDTO targetSchema = new TargetSchemaDTO();
        targetSchema.setTables(List.of(
                new TableInfo("users", columns)
        ));

        List<SchemaDifference> differences =
                comparisonService.compareSchemas(currentTables, targetSchema);

        assertTrue(differences.isEmpty());
    }

    @Test
    void detectsAddedColumn() {

        List<ColumnInfo> currentColumns = List.of(
                new ColumnInfo("id", "BIGINT", false, 19, null)
        );

        List<TableInfo> currentTables = List.of(
                new TableInfo("users", currentColumns)
        );

        List<ColumnInfo> targetColumns = List.of(
                new ColumnInfo("id", "BIGINT", false, 19, null),
                new ColumnInfo("email", "VARCHAR", true, 150, null)
        );

        TargetSchemaDTO targetSchema = new TargetSchemaDTO();
        targetSchema.setTables(List.of(
                new TableInfo("users", targetColumns)
        ));

        List<SchemaDifference> differences =
                comparisonService.compareSchemas(currentTables, targetSchema);

        assertEquals(1, differences.size());
        assertEquals("ADD_COLUMN", differences.get(0).getChangeType());
        assertEquals("email", differences.get(0).getColumnName());
    }

    @Test
    void detectsModifiedDataType() {

        List<TableInfo> currentTables = List.of(
                new TableInfo("users", List.of(
                        new ColumnInfo("name", "VARCHAR", true, 100, null)
                ))
        );

        TargetSchemaDTO targetSchema = new TargetSchemaDTO();
        targetSchema.setTables(List.of(
                new TableInfo("users", List.of(
                        new ColumnInfo("name", "VARCHAR", true, 200, null)
                ))
        ));

        List<SchemaDifference> differences =
                comparisonService.compareSchemas(currentTables, targetSchema);

        assertEquals(1, differences.size());
        assertEquals("MODIFY_DATA_TYPE", differences.get(0).getChangeType());
    }

    @Test
    void detectsRemovedColumn() {

        List<TableInfo> currentTables = List.of(
                new TableInfo("users", List.of(
                        new ColumnInfo("id", "BIGINT", false, 19, null),
                        new ColumnInfo("email", "VARCHAR", true, 150, null)
                ))
        );

        TargetSchemaDTO targetSchema = new TargetSchemaDTO();
        targetSchema.setTables(List.of(
                new TableInfo("users", List.of(
                        new ColumnInfo("id", "BIGINT", false, 19, null)
                ))
        ));

        List<SchemaDifference> differences =
                comparisonService.compareSchemas(currentTables, targetSchema);

        assertEquals(1, differences.size());
        assertEquals("REMOVE_COLUMN", differences.get(0).getChangeType());
        assertEquals("email", differences.get(0).getColumnName());
    }

    @Test
    void detectsRemovedTable() {

        List<TableInfo> currentTables = List.of(
                new TableInfo("users", List.of(
                        new ColumnInfo("id", "BIGINT", false, 19, null)
                ))
        );

        TargetSchemaDTO targetSchema = new TargetSchemaDTO();
        targetSchema.setTables(List.of());

        List<SchemaDifference> differences =
                comparisonService.compareSchemas(currentTables, targetSchema);

        assertEquals(1, differences.size());
        assertEquals("REMOVE_TABLE", differences.get(0).getChangeType());
    }
}
