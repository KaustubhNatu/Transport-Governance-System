package com.tgs.transport_governance_api.driverdocuments.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateDriverDocumentRequest {

    @NotNull(message = "Driver ID is required")
    private Integer driverId;

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;

    @FutureOrPresent(message = "Expiry date cannot be in the past")
    private LocalDate expiryDate;

    public CreateDriverDocumentRequest() {
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}