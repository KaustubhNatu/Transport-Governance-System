package com.tgs.transport_governance_api.invoices.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateInvoiceRequest {

    @NotNull
    private Integer studentId;

    @NotNull
    private Integer vendorId;

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;

    @NotNull
    private BigDecimal amount;

    private LocalDate dueDate;
}