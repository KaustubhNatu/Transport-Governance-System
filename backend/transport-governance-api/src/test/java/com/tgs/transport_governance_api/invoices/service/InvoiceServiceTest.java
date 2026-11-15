package com.tgs.transport_governance_api.invoices.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.invoices.dto.CreateInvoiceRequest;
import com.tgs.transport_governance_api.invoices.dto.InvoiceResponse;
import com.tgs.transport_governance_api.invoices.entity.Invoice;
import com.tgs.transport_governance_api.invoices.repository.InvoiceRepository;
import com.tgs.transport_governance_api.students.entity.Student;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import com.tgs.transport_governance_api.studenttransport.repository.StudentTransportRepository;
import com.tgs.transport_governance_api.vendors.entity.Vendor;
import com.tgs.transport_governance_api.vendors.repository.VendorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private StudentTransportRepository studentTransportRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    @DisplayName("createInvoice: successfully creates invoice when all rules pass")
    void createInvoice_success() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setMonth(5);
        request.setYear(2026);
        request.setAmount(new BigDecimal("150.00"));
        request.setDueDate(LocalDate.of(2026, 5, 25));

        StudentTransport assignment = new StudentTransport();
        assignment.setStudentId(1);
        assignment.setVendorId(2);
        assignment.setStatus("ACTIVE");

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.of(assignment));
        when(invoiceRepository.findByStudentIdAndMonthAndYear(1, 5, 2026)).thenReturn(Optional.empty());

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice inv = invocation.getArgument(0);
            inv.setId(100);
            return inv;
        });

        InvoiceResponse response = invoiceService.createInvoice(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100);
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getAmount()).isEqualByComparingTo("150.00");
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("createInvoice: throws ResourceNotFoundException when student not found")
    void createInvoice_studentNotFound() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(99);

        when(studentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.createInvoice(request));
    }

    @Test
    @DisplayName("createInvoice: throws ResourceNotFoundException when vendor not found")
    void createInvoice_vendorNotFound() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(1);
        request.setVendorId(99);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.createInvoice(request));
    }

    @Test
    @DisplayName("createInvoice: throws BadRequestException when student has no active transport assignment")
    void createInvoice_noActiveAssignment() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(1);
        request.setVendorId(2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> invoiceService.createInvoice(request));
        assertThat(ex.getMessage()).isEqualTo("Student has no active transport assignment");
    }

    @Test
    @DisplayName("createInvoice: throws BadRequestException when student is assigned to different vendor")
    void createInvoice_vendorMismatch() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(1);
        request.setVendorId(2);

        StudentTransport assignment = new StudentTransport();
        assignment.setVendorId(999); // different vendor

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.of(assignment));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> invoiceService.createInvoice(request));
        assertThat(ex.getMessage()).isEqualTo("Student is not assigned to this vendor");
    }

    @Test
    @DisplayName("createInvoice: throws BadRequestException when invoice already exists for month and year")
    void createInvoice_duplicateMonthYear() {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setMonth(5);
        request.setYear(2026);

        StudentTransport assignment = new StudentTransport();
        assignment.setVendorId(2);

        when(studentRepository.findById(1)).thenReturn(Optional.of(new Student()));
        when(vendorRepository.findById(2)).thenReturn(Optional.of(new Vendor()));
        when(studentTransportRepository.findByStudentIdAndStatus(1, "ACTIVE")).thenReturn(Optional.of(assignment));
        when(invoiceRepository.findByStudentIdAndMonthAndYear(1, 5, 2026)).thenReturn(Optional.of(new Invoice()));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> invoiceService.createInvoice(request));
        assertThat(ex.getMessage()).isEqualTo("Invoice already exists for this month");
    }

    @Test
    @DisplayName("getInvoiceById: returns invoice when found")
    void getInvoiceById_success() {
        Invoice invoice = new Invoice();
        invoice.setId(100);
        invoice.setAmount(new BigDecimal("150.00"));

        when(invoiceRepository.findById(100)).thenReturn(Optional.of(invoice));

        InvoiceResponse response = invoiceService.getInvoiceById(100);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100);
    }

    @Test
    @DisplayName("getInvoiceById: throws ResourceNotFoundException when not found")
    void getInvoiceById_notFound() {
        when(invoiceRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.getInvoiceById(999));
    }

    @Test
    @DisplayName("getAllInvoices: returns list of all invoices")
    void getAllInvoices_success() {
        Invoice invoice = new Invoice();
        invoice.setId(1);

        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        List<InvoiceResponse> list = invoiceService.getAllInvoices();

        assertThat(list).hasSize(1);
    }
}
