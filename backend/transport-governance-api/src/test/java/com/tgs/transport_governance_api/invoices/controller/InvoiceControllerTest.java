package com.tgs.transport_governance_api.invoices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.invoices.dto.CreateInvoiceRequest;
import com.tgs.transport_governance_api.invoices.dto.InvoiceResponse;
import com.tgs.transport_governance_api.invoices.service.InvoiceService;
import com.tgs.transport_governance_api.users.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
@Import({InvoiceController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private InvoiceService invoiceService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /invoices: allows VENDOR_ADMIN to create invoice")
    void createInvoice_success_asVendorAdmin() throws Exception {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(1);
        request.setVendorId(2);
        request.setMonth(5);
        request.setYear(2026);
        request.setAmount(new BigDecimal("100.00"));

        InvoiceResponse response = new InvoiceResponse();
        response.setId(10);
        response.setStatus("PENDING");
        response.setAmount(new BigDecimal("100.00"));

        when(invoiceService.createInvoice(any(CreateInvoiceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(invoiceService).createInvoice(any(CreateInvoiceRequest.class));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("POST /invoices: returns 403 Forbidden for PARENT")
    void createInvoice_forbidden_asParent() throws Exception {
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStudentId(1);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /invoices: allows SCHOOL_ADMIN to view invoices")
    void getAllInvoices_success_asSchoolAdmin() throws Exception {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(10);
        response.setStatus("PENDING");

        when(invoiceService.getAllInvoices()).thenReturn(List.of(response));

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("GET /invoices: returns 403 Forbidden for PARENT")
    void getAllInvoices_forbidden_asParent() throws Exception {
        mockMvc.perform(get("/invoices"))
                .andExpect(status().isForbidden());
    }
}
