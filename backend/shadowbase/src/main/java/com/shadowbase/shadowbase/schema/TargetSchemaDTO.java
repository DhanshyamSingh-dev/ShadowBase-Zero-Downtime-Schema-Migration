
package com.shadowbase.shadowbase.schema;

import java.util.List;

public class TargetSchemaDTO {

    private String schemaName;
    private List<TableInfo> tables;

    // Default constructor for JSON conversion
    public TargetSchemaDTO() {
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public List<TableInfo> getTables() {
        return tables;
    }

    public void setTables(List<TableInfo> tables) {
        this.tables = tables;
    }
}
