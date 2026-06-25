package com.tgs.transport_governance_api.routes.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RouteResponse {

    private Integer id;

    private Integer vendorId;

    private String routeName;

    private String startLocation;

    private String endLocation;

    private Integer vehicleId;

    private Integer driverId;

    private LocalDateTime createdAt;
}