package com.tgs.transport_governance_api.parents.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ParentResponse {

    private Integer id;

    private Integer userId;

    private String address;

    private String emergencyContact;

    private LocalDateTime createdAt;
}