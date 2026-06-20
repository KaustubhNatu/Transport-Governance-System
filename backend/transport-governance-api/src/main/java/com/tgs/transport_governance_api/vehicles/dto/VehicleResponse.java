package com.tgs.transport_governance_api.vehicles.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VehicleResponse {

    private Integer id;

    private Integer vendorId;

    private String registrationNumber;

    private Integer capacity;

    private String make;

    private String model;

    private Integer manufactureYear;

    private LocalDateTime createdAt;
}