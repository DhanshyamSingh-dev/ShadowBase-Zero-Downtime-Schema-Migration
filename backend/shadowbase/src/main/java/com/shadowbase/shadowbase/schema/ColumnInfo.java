
package com.shadowbase.shadowbase.schema;

public class ColumnInfo {

    private String columnName;
    private String dataType;
    private boolean nullable;
    private Integer length;
    private String defaultValue;

    // Default constructor for JSON conversion
    public ColumnInfo() {
    }

    // Existing constructor
    public ColumnInfo(String columnName, String dataType, boolean nullable) {
        this(columnName, dataType, nullable, null, null);
    }

    // Parameterized constructor
    public ColumnInfo(String columnName, String dataType, boolean nullable,
                      Integer length, String defaultValue) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.nullable = nullable;
        this.length = length;
        this.defaultValue = defaultValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
