package com.tgs.transport_governance_api.vendors.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
@Getter
@Setter
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "school_id", nullable = false)
    private Integer schoolId;

    @Column(name = "owner_user_id")
    private Integer ownerUserId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
