
package com.shadowbase.shadowbase.schema;

public class ColumnInfo {

    private String columnName;
    private String dataType;
    private boolean nullable;

    public ColumnInfo(String columnName, String dataType, boolean nullable) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.nullable = nullable;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean isNullable() {
        return nullable;
    }
}