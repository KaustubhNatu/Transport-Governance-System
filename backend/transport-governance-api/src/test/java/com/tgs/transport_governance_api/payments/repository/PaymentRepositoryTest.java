package com.tgs.transport_governance_api.payments.repository;

import com.tgs.transport_governance_api.payments.entity.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByInvoiceId: finds payment by invoice ID")
    void findByInvoiceId_found() {
        Payment payment = new Payment();
        payment.setInvoiceId(10);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentMethod("BANK_TRANSFER");
        payment.setTransactionReference("REF-789");
        payment.setRecordedBy(1);
        payment.setPaidAt(LocalDateTime.now());
        entityManager.persistAndFlush(payment);

        Optional<Payment> found = paymentRepository.findByInvoiceId(10);

        assertThat(found).isPresent();
        assertThat(found.get().getInvoiceId()).isEqualTo(10);
        assertThat(found.get().getTransactionReference()).isEqualTo("REF-789");
    }

    @Test
    @DisplayName("findByInvoiceId: returns empty when no payment for invoice ID exists")
    void findByInvoiceId_notFound() {
        Optional<Payment> found = paymentRepository.findByInvoiceId(999);
        assertThat(found).isEmpty();
    }
}
