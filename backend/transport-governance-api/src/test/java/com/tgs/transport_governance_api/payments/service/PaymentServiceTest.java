package com.tgs.transport_governance_api.payments.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.invoices.entity.Invoice;
import com.tgs.transport_governance_api.invoices.repository.InvoiceRepository;
import com.tgs.transport_governance_api.payments.dto.CreatePaymentRequest;
import com.tgs.transport_governance_api.payments.dto.PaymentResponse;
import com.tgs.transport_governance_api.payments.entity.Payment;
import com.tgs.transport_governance_api.payments.repository.PaymentRepository;
import com.tgs.transport_governance_api.users.entity.User;
import com.tgs.transport_governance_api.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("createPayment: successfully records payment and updates invoice status to PAID")
    void createPayment_success() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(10);
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod("CREDIT_CARD");
        request.setTransactionReference("TXN-12345");
        request.setRecordedBy(1);

        Invoice invoice = new Invoice();
        invoice.setId(10);
        invoice.setAmount(new BigDecimal("100.00"));
        invoice.setStatus("PENDING");

        when(invoiceRepository.findById(10)).thenReturn(Optional.of(invoice));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(paymentRepository.findByInvoiceId(10)).thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(50);
            return p;
        });

        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(50);
        assertThat(response.getAmount()).isEqualByComparingTo("100.00");
        assertThat(invoice.getStatus()).isEqualTo("PAID");

        verify(invoiceRepository).save(invoice);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("createPayment: throws ResourceNotFoundException when invoice not found")
    void createPayment_invoiceNotFound() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(99);

        when(invoiceRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(request));
    }

    @Test
    @DisplayName("createPayment: throws ResourceNotFoundException when recordedBy user not found")
    void createPayment_userNotFound() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(10);
        request.setRecordedBy(99);

        when(invoiceRepository.findById(10)).thenReturn(Optional.of(new Invoice()));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.createPayment(request));
    }

    @Test
    @DisplayName("createPayment: throws BadRequestException when invoice status is not PENDING")
    void createPayment_invoiceAlreadyPaid() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(10);
        request.setRecordedBy(1);

        Invoice invoice = new Invoice();
        invoice.setId(10);
        invoice.setStatus("PAID");

        when(invoiceRepository.findById(10)).thenReturn(Optional.of(invoice));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> paymentService.createPayment(request));
        assertThat(ex.getMessage()).isEqualTo("Invoice is already paid");
    }

    @Test
    @DisplayName("createPayment: throws BadRequestException when payment already exists for invoice")
    void createPayment_duplicatePayment() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(10);
        request.setRecordedBy(1);

        Invoice invoice = new Invoice();
        invoice.setId(10);
        invoice.setStatus("PENDING");

        when(invoiceRepository.findById(10)).thenReturn(Optional.of(invoice));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(paymentRepository.findByInvoiceId(10)).thenReturn(Optional.of(new Payment()));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> paymentService.createPayment(request));
        assertThat(ex.getMessage()).isEqualTo("Payment already exists for this invoice");
    }

    @Test
    @DisplayName("createPayment: throws BadRequestException when payment amount does not match invoice amount")
    void createPayment_amountMismatch() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(10);
        request.setAmount(new BigDecimal("50.00"));
        request.setRecordedBy(1);

        Invoice invoice = new Invoice();
        invoice.setId(10);
        invoice.setAmount(new BigDecimal("100.00"));
        invoice.setStatus("PENDING");

        when(invoiceRepository.findById(10)).thenReturn(Optional.of(invoice));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User()));
        when(paymentRepository.findByInvoiceId(10)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> paymentService.createPayment(request));
        assertThat(ex.getMessage()).isEqualTo("Payment amount must match invoice amount");
    }

    @Test
    @DisplayName("getPaymentById: returns payment when found")
    void getPaymentById_success() {
        Payment payment = new Payment();
        payment.setId(50);
        payment.setAmount(new BigDecimal("100.00"));

        when(paymentRepository.findById(50)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentById(50);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(50);
    }

    @Test
    @DisplayName("getPaymentById: throws ResourceNotFoundException when not found")
    void getPaymentById_notFound() {
        when(paymentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentById(99));
    }

    @Test
    @DisplayName("getAllPayments: returns list of all payments")
    void getAllPayments_success() {
        Payment payment = new Payment();
        payment.setId(1);

        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<PaymentResponse> list = paymentService.getAllPayments();

        assertThat(list).hasSize(1);
    }
}

