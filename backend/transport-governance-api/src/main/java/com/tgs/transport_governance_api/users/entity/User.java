package com.tgs.transport_governance_api.users.entity;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private String status;

    private Integer schoolId;
    private Integer vendorId;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}

