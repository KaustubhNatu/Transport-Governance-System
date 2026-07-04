package com.tgs.transport_governance_api.invoices.repository;

import com.tgs.transport_governance_api.invoices.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository
        extends JpaRepository<Invoice, Integer> {

    Optional<Invoice> findByStudentIdAndMonthAndYear(
            Integer studentId,
            Integer month,
            Integer year);

}