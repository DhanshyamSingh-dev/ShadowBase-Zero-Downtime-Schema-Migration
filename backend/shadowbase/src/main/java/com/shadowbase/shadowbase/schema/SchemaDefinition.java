
package com.shadowbase.shadowbase.schema;

import java.util.List;

public class SchemaDefinition {

    private List<String> currentTables;
    private List<String> targetTables;

    public List<String> getCurrentTables() {
        return currentTables;
    }

    public void setCurrentTables(List<String> currentTables) {
        this.currentTables = currentTables;
    }

    public List<String> getTargetTables() {
        return targetTables;
    }

    public void setTargetTables(List<String> targetTables) {
        this.targetTables = targetTables;
    }
}
