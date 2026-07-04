package com.tgs.transport_governance_api.invoices.controller;

import com.tgs.transport_governance_api.invoices.dto.CreateInvoiceRequest;
import com.tgs.transport_governance_api.invoices.dto.InvoiceResponse;
import com.tgs.transport_governance_api.invoices.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping
    public InvoiceResponse createInvoice(
            @RequestBody @Valid
            CreateInvoiceRequest request) {

        return invoiceService.createInvoice(request);
    }

    @GetMapping
    public List<InvoiceResponse> getAllInvoices() {

        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoiceById(
            @PathVariable Integer id) {

        return invoiceService.getInvoiceById(id);
    }
}