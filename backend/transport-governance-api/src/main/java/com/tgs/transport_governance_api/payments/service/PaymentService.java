package com.tgs.transport_governance_api.payments.service;

import com.tgs.transport_governance_api.invoices.entity.Invoice;
import com.tgs.transport_governance_api.invoices.repository.InvoiceRepository;
import com.tgs.transport_governance_api.payments.dto.CreatePaymentRequest;
import com.tgs.transport_governance_api.payments.dto.PaymentResponse;
import com.tgs.transport_governance_api.payments.entity.Payment;
import com.tgs.transport_governance_api.payments.repository.PaymentRepository;
import com.tgs.transport_governance_api.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PaymentResponse createPayment(
            CreatePaymentRequest request) {

        Invoice invoice = invoiceRepository.findById(
                        request.getInvoiceId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invoice not found"));

        userRepository.findById(request.getRecordedBy())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        if (!invoice.getStatus().equals("PENDING")) {
            throw new RuntimeException(
                    "Invoice is already paid");
        }

        if (paymentRepository.findByInvoiceId(
                request.getInvoiceId()).isPresent()) {

            throw new RuntimeException(
                    "Payment already exists for this invoice");
        }

        if (invoice.getAmount().compareTo(
                request.getAmount()) != 0) {

            throw new RuntimeException(
                    "Payment amount must match invoice amount");
        }

        Payment payment = new Payment();

        payment.setInvoiceId(request.getInvoiceId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(
                request.getPaymentMethod());
        payment.setTransactionReference(
                request.getTransactionReference());
        payment.setRecordedBy(
                request.getRecordedBy());

        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment =
                paymentRepository.save(payment);

        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        return mapToResponse(savedPayment);
    }

    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PaymentResponse getPaymentById(
            Integer id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"));

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(
            Payment payment) {

        PaymentResponse response =
                new PaymentResponse();

        response.setId(payment.getId());
        response.setInvoiceId(payment.getInvoiceId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(
                payment.getPaymentMethod());
        response.setTransactionReference(
                payment.getTransactionReference());
        response.setPaidAt(payment.getPaidAt());
        response.setRecordedBy(
                payment.getRecordedBy());

        return response;
    }
}