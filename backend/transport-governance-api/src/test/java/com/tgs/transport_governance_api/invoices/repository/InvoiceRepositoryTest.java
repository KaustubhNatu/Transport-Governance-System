package com.tgs.transport_governance_api.invoices.repository;

import com.tgs.transport_governance_api.invoices.entity.Invoice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByStudentIdAndMonthAndYear: finds invoice by student ID, month, and year")
    void findByStudentIdAndMonthAndYear_found() {
        Invoice invoice = new Invoice();
        invoice.setStudentId(1);
        invoice.setVendorId(2);
        invoice.setMonth(5);
        invoice.setYear(2026);
        invoice.setAmount(new BigDecimal("120.00"));
        invoice.setStatus("PENDING");
        invoice.setDueDate(LocalDate.of(2026, 5, 20));
        invoice.setGeneratedAt(LocalDateTime.now());
        entityManager.persistAndFlush(invoice);

        Optional<Invoice> found = invoiceRepository.findByStudentIdAndMonthAndYear(1, 5, 2026);

        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualByComparingTo("120.00");
    }

    @Test
    @DisplayName("findByStudentIdAndMonthAndYear: returns empty when no matching invoice exists")
    void findByStudentIdAndMonthAndYear_notFound() {
        Optional<Invoice> found = invoiceRepository.findByStudentIdAndMonthAndYear(99, 1, 2026);
        assertThat(found).isEmpty();
    }
}

