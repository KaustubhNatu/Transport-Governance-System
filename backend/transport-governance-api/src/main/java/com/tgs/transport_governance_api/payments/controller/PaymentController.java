package com.tgs.transport_governance_api.payments.controller;

import com.tgs.transport_governance_api.payments.dto.CreatePaymentRequest;
import com.tgs.transport_governance_api.payments.dto.PaymentResponse;
import com.tgs.transport_governance_api.payments.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(
            @RequestBody @Valid
            CreatePaymentRequest request) {

        return paymentService.createPayment(request);
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {

        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(
            @PathVariable Integer id) {

        return paymentService.getPaymentById(id);
    }
}