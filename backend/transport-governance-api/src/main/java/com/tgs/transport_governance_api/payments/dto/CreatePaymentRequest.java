package com.tgs.transport_governance_api.payments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull
    private Integer invoiceId;

    @NotNull
    private BigDecimal amount;

    private String paymentMethod;

    private String transactionReference;

    @NotNull
    private Integer recordedBy;
}