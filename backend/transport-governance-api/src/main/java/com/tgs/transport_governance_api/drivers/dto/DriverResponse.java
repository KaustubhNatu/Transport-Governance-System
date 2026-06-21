package com.tgs.transport_governance_api.drivers.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class DriverResponse {

    private Integer id;

    private Integer userId;

    private Integer vendorId;

    private String licenseNumber;

    private LocalDate licenseExpiry;

    private Boolean backgroundVerified;

    private LocalDateTime createdAt;
}