package com.tgs.transport_governance_api.transfernotification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.transfernotification.dto.CreateTransferNotificationRequest;
import com.tgs.transport_governance_api.transfernotification.dto.TransferEventResponse;
import com.tgs.transport_governance_api.transfernotification.dto.TransferNotificationResponse;
import com.tgs.transport_governance_api.transfernotification.dto.UpdateDuesRequest;
import com.tgs.transport_governance_api.transfernotification.service.TransferNotificationService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferNotificationController.class)
@Import({TransferNotificationController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class TransferNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferNotificationService transferNotificationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should create transfer notification successfully when caller is SCHOOL_ADMIN")
    void createNotification_success_asSchoolAdmin() throws Exception {
        CreateTransferNotificationRequest request = new CreateTransferNotificationRequest();
        request.setStudentId(1);

        TransferNotificationResponse response = new TransferNotificationResponse();
        response.setId(10);
        response.setStudentId(1);
        response.setVendorId(20);
        response.setDuesStatus("UNKNOWN");
        response.setDuesAmount(BigDecimal.ZERO);

        when(transferNotificationService.createNotification(any(CreateTransferNotificationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/transfer-notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.duesStatus").value("UNKNOWN"));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should return 403 Forbidden when creating transfer notification as VENDOR_ADMIN")
    void createNotification_forbidden_asVendorAdmin() throws Exception {
        CreateTransferNotificationRequest request = new CreateTransferNotificationRequest();
        request.setStudentId(1);

        mockMvc.perform(post("/transfer-notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("Should get all notifications successfully when caller is SUPER_ADMIN")
    void getAllNotifications_success_asSuperAdmin() throws Exception {
        TransferNotificationResponse response = new TransferNotificationResponse();
        response.setId(10);
        response.setStudentId(1);

        when(transferNotificationService.getAllNotifications()).thenReturn(List.of(response));

        mockMvc.perform(get("/transfer-notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should get notification by ID successfully when caller is VENDOR_ADMIN")
    void getNotificationById_success_asVendorAdmin() throws Exception {
        TransferNotificationResponse response = new TransferNotificationResponse();
        response.setId(10);
        response.setStudentId(1);

        when(transferNotificationService.getNotificationById(10)).thenReturn(response);

        mockMvc.perform(get("/transfer-notifications/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should update dues successfully when caller is VENDOR_ADMIN")
    void updateDues_success_asVendorAdmin() throws Exception {
        UpdateDuesRequest request = new UpdateDuesRequest();
        request.setDuesStatus("CLEAR");
        request.setDuesAmount(BigDecimal.ZERO);
        request.setNotes("Payment settled");

        TransferNotificationResponse response = new TransferNotificationResponse();
        response.setId(10);
        response.setDuesStatus("CLEAR");
        response.setDuesAmount(BigDecimal.ZERO);

        when(transferNotificationService.updateDues(eq(10), any(UpdateDuesRequest.class))).thenReturn(response);

        mockMvc.perform(put("/transfer-notifications/10/dues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duesStatus").value("CLEAR"));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should return 403 Forbidden when updating dues as SCHOOL_ADMIN")
    void updateDues_forbidden_asSchoolAdmin() throws Exception {
        UpdateDuesRequest request = new UpdateDuesRequest();
        request.setDuesStatus("CLEAR");
        request.setDuesAmount(BigDecimal.ZERO);

        mockMvc.perform(put("/transfer-notifications/10/dues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should complete notification successfully when caller is SCHOOL_ADMIN")
    void completeNotification_success_asSchoolAdmin() throws Exception {
        doNothing().when(transferNotificationService).completeNotification(10);

        mockMvc.perform(put("/transfer-notifications/10/complete"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should return 403 Forbidden when completing notification as VENDOR_ADMIN")
    void completeNotification_forbidden_asVendorAdmin() throws Exception {
        mockMvc.perform(put("/transfer-notifications/10/complete"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("Should get notification events successfully")
    void getNotificationEvents_success() throws Exception {
        TransferEventResponse event = new TransferEventResponse();
        event.setId(1);
        event.setEventType("NOTIFICATION_CREATED");
        event.setNotes("Created");
        event.setPerformedBy(5);
        event.setCreatedAt(LocalDateTime.now());

        when(transferNotificationService.getNotificationEvents(10)).thenReturn(List.of(event));

        mockMvc.perform(get("/transfer-notifications/10/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].eventType").value("NOTIFICATION_CREATED"));
    }

    @Test
    @DisplayName("Should return 403 Forbidden when unauthenticated")
    void getNotificationById_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(get("/transfer-notifications/10"))
                .andExpect(status().isForbidden());
    }
}

