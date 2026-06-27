package com.tgs.transport_governance_api.parents.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "parents")
@Getter
@Setter
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    private String address;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}