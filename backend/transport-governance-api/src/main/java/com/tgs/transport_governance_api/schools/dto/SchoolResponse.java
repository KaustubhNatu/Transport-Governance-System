package com.tgs.transport_governance_api.schools.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SchoolResponse {

    private Integer id;

    private String name;

    private String address;

    private String contactEmail;

    private String contactPhone;

    private LocalDateTime createdAt;

}