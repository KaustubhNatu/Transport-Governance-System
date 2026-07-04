package com.tgs.transport_governance_api.invoices.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private BigDecimal amount;

    private String status;

    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;

    @Column(name = "due_date")
    private LocalDate dueDate;
}