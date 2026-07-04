package com.tgs.transport_governance_api.payments.repository;

import com.tgs.transport_governance_api.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByInvoiceId(
            Integer invoiceId);

}