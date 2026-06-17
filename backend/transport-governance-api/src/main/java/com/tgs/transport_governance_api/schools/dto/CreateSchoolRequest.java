package com.tgs.transport_governance_api.schools.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSchoolRequest {

    @NotBlank
    private String name;

    private String address;

    @Email
    private String contactEmail;

    private String contactPhone;

}