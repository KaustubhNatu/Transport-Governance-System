package com.tgs.transport_governance_api.transfernotification.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferNotificationResponse {

    private Integer id;

    private Integer studentId;

    private Integer vendorId;

    private String duesStatus;

    private BigDecimal duesAmount;

    private Integer createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime vendorNotifiedAt;

    private LocalDateTime parentNotifiedAt;

    private LocalDateTime completedAt;

    public TransferNotificationResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getVendorNotifiedAt() {
        return vendorNotifiedAt;
    }

    public void setVendorNotifiedAt(LocalDateTime vendorNotifiedAt) {
        this.vendorNotifiedAt = vendorNotifiedAt;
    }

    public LocalDateTime getParentNotifiedAt() {
        return parentNotifiedAt;
    }

    public void setParentNotifiedAt(LocalDateTime parentNotifiedAt) {
        this.parentNotifiedAt = parentNotifiedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}