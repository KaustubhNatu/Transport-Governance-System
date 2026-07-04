package com.tgs.transport_governance_api.invoices.service;

import com.tgs.transport_governance_api.invoices.dto.CreateInvoiceRequest;
import com.tgs.transport_governance_api.invoices.dto.InvoiceResponse;
import com.tgs.transport_governance_api.invoices.entity.Invoice;
import com.tgs.transport_governance_api.invoices.repository.InvoiceRepository;
import com.tgs.transport_governance_api.studenttransport.entity.StudentTransport;
import com.tgs.transport_governance_api.studenttransport.repository.StudentTransportRepository;
import com.tgs.transport_governance_api.students.repository.StudentRepository;
import com.tgs.transport_governance_api.vendors.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private StudentTransportRepository studentTransportRepository;

    public InvoiceResponse createInvoice(
            CreateInvoiceRequest request) {

        studentRepository.findById(request.getStudentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student not found"));

        vendorRepository.findById(request.getVendorId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vendor not found"));

        StudentTransport assignment =
                studentTransportRepository
                        .findByStudentIdAndStatus(
                                request.getStudentId(),
                                "ACTIVE")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student has no active transport assignment"));

        if (!assignment.getVendorId().equals(
                request.getVendorId())) {

            throw new RuntimeException(
                    "Student is not assigned to this vendor");
        }

        if (invoiceRepository
                .findByStudentIdAndMonthAndYear(
                        request.getStudentId(),
                        request.getMonth(),
                        request.getYear())
                .isPresent()) {

            throw new RuntimeException(
                    "Invoice already exists for this month");
        }

        Invoice invoice = new Invoice();

        invoice.setStudentId(request.getStudentId());
        invoice.setVendorId(request.getVendorId());
        invoice.setMonth(request.getMonth());
        invoice.setYear(request.getYear());
        invoice.setAmount(request.getAmount());
        invoice.setDueDate(request.getDueDate());

        invoice.setStatus("PENDING");
        invoice.setGeneratedAt(LocalDateTime.now());

        Invoice savedInvoice =
                invoiceRepository.save(invoice);

        return mapToResponse(savedInvoice);
    }

    public List<InvoiceResponse> getAllInvoices() {

        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public InvoiceResponse getInvoiceById(
            Integer id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invoice not found"));

        return mapToResponse(invoice);
    }

    private InvoiceResponse mapToResponse(
            Invoice invoice) {

        InvoiceResponse response =
                new InvoiceResponse();

        response.setId(invoice.getId());
        response.setStudentId(invoice.getStudentId());
        response.setVendorId(invoice.getVendorId());
        response.setMonth(invoice.getMonth());
        response.setYear(invoice.getYear());
        response.setAmount(invoice.getAmount());
        response.setStatus(invoice.getStatus());
        response.setGeneratedAt(invoice.getGeneratedAt());
        response.setDueDate(invoice.getDueDate());

        return response;
    }
}