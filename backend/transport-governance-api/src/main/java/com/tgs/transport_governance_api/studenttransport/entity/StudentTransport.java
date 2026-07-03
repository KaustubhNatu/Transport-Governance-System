package com.tgs.transport_governance_api.studenttransport.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_transport")
@Getter
@Setter
public class StudentTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(name = "route_id")
    private Integer routeId;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}