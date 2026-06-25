package com.tgs.transport_governance_api.routes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
@Getter
@Setter
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(name = "route_name", nullable = false)
    private String routeName;

    @Column(name = "start_location")
    private String startLocation;

    @Column(name = "end_location")
    private String endLocation;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}