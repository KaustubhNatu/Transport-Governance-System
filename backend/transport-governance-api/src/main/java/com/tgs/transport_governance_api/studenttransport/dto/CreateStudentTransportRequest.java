package com.tgs.transport_governance_api.studenttransport.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudentTransportRequest {

    @NotNull
    private Integer studentId;

    @NotNull
    private Integer vendorId;

    private Integer routeId;

    private Integer vehicleId;
}