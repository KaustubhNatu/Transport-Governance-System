package com.tgs.transport_governance_api.payments.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Integer id;

    private Integer invoiceId;

    private BigDecimal amount;

    private String paymentMethod;

    private String transactionReference;

    private LocalDateTime paidAt;

    private Integer recordedBy;
}