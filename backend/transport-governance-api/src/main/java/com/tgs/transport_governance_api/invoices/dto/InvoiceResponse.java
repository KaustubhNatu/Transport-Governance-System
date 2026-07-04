package com.tgs.transport_governance_api.invoices.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceResponse {

    private Integer id;

    private Integer studentId;

    private Integer vendorId;

    private Integer month;

    private Integer year;

    private BigDecimal amount;

    private String status;

    private LocalDateTime generatedAt;

    private LocalDate dueDate;
}