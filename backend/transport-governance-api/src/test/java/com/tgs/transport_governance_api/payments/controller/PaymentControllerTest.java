package com.tgs.transport_governance_api.payments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.payments.dto.CreatePaymentRequest;
import com.tgs.transport_governance_api.payments.dto.PaymentResponse;
import com.tgs.transport_governance_api.payments.service.PaymentService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import({PaymentController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should create payment successfully when caller is VENDOR_ADMIN")
    void createPayment_success_asVendorAdmin() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(1);
        request.setAmount(new BigDecimal("500.00"));
        request.setPaymentMethod("BANK_TRANSFER");
        request.setTransactionReference("TXN-12345");
        request.setRecordedBy(10);

        PaymentResponse response = new PaymentResponse();
        response.setId(1);
        response.setInvoiceId(1);
        response.setAmount(new BigDecimal("500.00"));
        response.setPaymentMethod("BANK_TRANSFER");
        response.setTransactionReference("TXN-12345");
        response.setPaidAt(LocalDateTime.now());
        response.setRecordedBy(10);

        when(paymentService.createPayment(any(CreatePaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.transactionReference").value("TXN-12345"));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should return 400 Bad Request when required payment fields are missing")
    void createPayment_validationFailure_missingFields() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("Should return 403 Forbidden when caller is PARENT")
    void createPayment_forbidden_asParent() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setInvoiceId(1);
        request.setAmount(new BigDecimal("500.00"));
        request.setRecordedBy(10);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should get all payments successfully when caller is SCHOOL_ADMIN")
    void getAllPayments_success_asSchoolAdmin() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setId(1);
        response.setAmount(new BigDecimal("500.00"));

        when(paymentService.getAllPayments()).thenReturn(List.of(response));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(500.00));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("Should get payment by ID successfully when caller is SUPER_ADMIN")
    void getPaymentById_success_asSuperAdmin() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setId(1);
        response.setAmount(new BigDecimal("500.00"));

        when(paymentService.getPaymentById(1)).thenReturn(response);

        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    @DisplayName("Should return 403 Forbidden when caller is unauthenticated")
    void getPaymentById_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isForbidden());
    }
}
