package com.tgs.transport_governance_api.transfernotification.dto;

import java.math.BigDecimal;

public class UpdateDuesRequest {

    private String duesStatus;

    private BigDecimal duesAmount;

    private String notes;

    public UpdateDuesRequest() {
    }

    public String getDuesStatus() {
        return duesStatus;
    }

    public void setDuesStatus(String duesStatus) {
        this.duesStatus = duesStatus;
    }

    public BigDecimal getDuesAmount() {
        return duesAmount;
    }

    public void setDuesAmount(BigDecimal duesAmount) {
        this.duesAmount = duesAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}