package com.tgs.transport_governance_api.vendors.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVendorRequest {

    @NotNull
    private Integer schoolId;

    private Integer ownerUserId;

    @NotBlank
    private String companyName;

    private String contactPhone;
}