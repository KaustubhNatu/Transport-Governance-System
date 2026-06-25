package com.tgs.transport_governance_api.routes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRouteRequest {

    @NotNull
    private Integer vendorId;

    @NotBlank
    private String routeName;

    private String startLocation;

    private String endLocation;

    private Integer vehicleId;

    private Integer driverId;
}