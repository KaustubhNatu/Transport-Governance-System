package com.tgs.transport_governance_api.users.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String status;
    private Set<String> roles;
}
