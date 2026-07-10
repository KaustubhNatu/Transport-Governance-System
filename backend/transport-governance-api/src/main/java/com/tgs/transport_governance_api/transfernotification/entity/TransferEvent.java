package com.tgs.transport_governance_api.transfernotification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "transfer_events")
public class TransferEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "notification_id", nullable = false)
    private Integer notificationId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "performed_by")
    private Integer performedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public TransferEvent() {
    }

}