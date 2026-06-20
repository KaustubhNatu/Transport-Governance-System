package com.tgs.transport_governance_api.vehicles.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;

    private Integer capacity;

    private String make;

    private String model;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}