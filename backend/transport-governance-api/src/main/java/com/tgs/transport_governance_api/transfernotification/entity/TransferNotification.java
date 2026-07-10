package com.tgs.transport_governance_api.transfernotification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "transfer_notifications")
public class TransferNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(name = "dues_status")
    private String duesStatus;

    @Column(name = "dues_amount")
    private BigDecimal duesAmount;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "vendor_notified_at")
    private LocalDateTime vendorNotifiedAt;

    @Column(name = "parent_notified_at")
    private LocalDateTime parentNotifiedAt;

    public TransferNotification() {
    }

}