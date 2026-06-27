package com.tgs.transport_governance_api.parents.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateParentRequest {

    @NotNull
    private Integer userId;

    private String address;

    private String emergencyContact;
}