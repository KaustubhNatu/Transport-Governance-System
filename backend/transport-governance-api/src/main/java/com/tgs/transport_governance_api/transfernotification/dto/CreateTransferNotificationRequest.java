package com.tgs.transport_governance_api.transfernotification.dto;

public class CreateTransferNotificationRequest {

    private Integer studentId;

    public CreateTransferNotificationRequest() {
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
}