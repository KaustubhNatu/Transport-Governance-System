package com.tgs.transport_governance_api.drivers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDriverRequest {

    private Integer userId;

    @NotNull
    private Integer vendorId;

    @NotBlank
    private String licenseNumber;

    private LocalDate licenseExpiry;

    private Boolean backgroundVerified;
}