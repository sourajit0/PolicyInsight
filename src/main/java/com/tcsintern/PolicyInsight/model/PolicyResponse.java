package com.tcsintern.PolicyInsight.model;

public class PolicyResponse {
    private String htmlTable;
    private boolean success;
    private String errorMessage;

    public PolicyResponse(String htmlTable) {
        this.htmlTable = htmlTable;
        this.success = true;
    }

    public PolicyResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    // Explicit getter methods
    public String getHtmlTable() {
        return htmlTable;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Setter methods if needed
    public void setHtmlTable(String htmlTable) {
        this.htmlTable = htmlTable;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}