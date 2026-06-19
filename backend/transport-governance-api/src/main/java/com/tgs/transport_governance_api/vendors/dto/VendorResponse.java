package com.tgs.transport_governance_api.vendors.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VendorResponse {

    private Integer id;

    private Integer schoolId;

    private Integer ownerUserId;

    private String companyName;

    private String contactPhone;

    private LocalDateTime createdAt;
}