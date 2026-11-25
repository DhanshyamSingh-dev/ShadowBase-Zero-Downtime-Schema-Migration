package com.shadowbase.shadowbase.schema;

public class SchemaDifference {

    private String changeType;
    private String tableName;
    private String columnName;
    private String currentValue;
    private String targetValue;

    public SchemaDifference() {
    }

    public SchemaDifference(String changeType, String tableName,
            String columnName, String currentValue, String targetValue) {

        this.changeType = changeType;
        this.tableName = tableName;
        this.columnName = columnName;
        this.currentValue = currentValue;
        this.targetValue = targetValue;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }
}