package com.tgs.transport_governance_api.studenttransport.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StudentTransportResponse {

    private Integer id;

    private Integer studentId;

    private Integer vendorId;

    private Integer routeId;

    private Integer vehicleId;

    private LocalDate enrollmentDate;

    private String status;

    private LocalDateTime createdAt;
}