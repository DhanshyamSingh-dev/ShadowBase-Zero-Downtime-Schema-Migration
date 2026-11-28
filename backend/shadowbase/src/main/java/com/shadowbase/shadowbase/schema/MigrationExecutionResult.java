
package com.shadowbase.shadowbase.schema;

import java.time.LocalDateTime;
import java.util.List;

public class MigrationExecutionResult {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String errorMessage;
    private List<String> executedStatements;
    private boolean schemaVerified;
    private String verificationMessage;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getExecutedStatements() {
        return executedStatements;
    }

    public void setExecutedStatements(List<String> executedStatements) {
        this.executedStatements = executedStatements;
    }

    public boolean isSchemaVerified() {
        return schemaVerified;
    }

    public void setSchemaVerified(boolean schemaVerified) {
        this.schemaVerified = schemaVerified;
    }

    public String getVerificationMessage() {
        return verificationMessage;
    }

    public void setVerificationMessage(String verificationMessage) {
        this.verificationMessage = verificationMessage;
    }
}
