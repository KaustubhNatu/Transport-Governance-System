package com.tgs.transport_governance_api.vehicles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleRequest {

    @NotNull
    private Integer vendorId;

    @NotBlank
    private String registrationNumber;

    private Integer capacity;

    private String make;

    private String model;

    private Integer manufactureYear;
}