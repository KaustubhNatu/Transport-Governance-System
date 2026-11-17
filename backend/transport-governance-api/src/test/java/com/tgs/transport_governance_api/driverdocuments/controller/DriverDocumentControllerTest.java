package com.tgs.transport_governance_api.driverdocuments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.driverdocuments.dto.CreateDriverDocumentRequest;
import com.tgs.transport_governance_api.driverdocuments.dto.DriverDocumentResponse;
import com.tgs.transport_governance_api.driverdocuments.service.DriverDocumentService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverDocumentController.class)
@Import({DriverDocumentController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class DriverDocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverDocumentService driverDocumentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should create driver document successfully when caller is VENDOR_ADMIN")
    void createDocument_success_asVendorAdmin() throws Exception {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("LICENSE");
        request.setDocumentUrl("https://example.com/license.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(1));

        DriverDocumentResponse response = new DriverDocumentResponse();
        response.setId(10);
        response.setDriverId(1);
        response.setDocumentType("LICENSE");
        response.setDocumentUrl("https://example.com/license.pdf");
        response.setExpiryDate(request.getExpiryDate());
        response.setUploadedAt(LocalDateTime.now());

        when(driverDocumentService.createDocument(any(CreateDriverDocumentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/driver-documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.documentType").value("LICENSE"));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should return 400 Bad Request when validation fails on document creation")
    void createDocument_validationFailure_missingFields() throws Exception {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();

        mockMvc.perform(post("/driver-documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should return 403 Forbidden when creating document as SCHOOL_ADMIN")
    void createDocument_forbidden_asSchoolAdmin() throws Exception {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("LICENSE");
        request.setDocumentUrl("https://example.com/license.pdf");

        mockMvc.perform(post("/driver-documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should update driver document successfully when caller is VENDOR_ADMIN")
    void updateDocument_success_asVendorAdmin() throws Exception {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("LICENSE");
        request.setDocumentUrl("https://example.com/license-updated.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(2));

        DriverDocumentResponse response = new DriverDocumentResponse();
        response.setId(10);
        response.setDriverId(1);
        response.setDocumentType("LICENSE");
        response.setDocumentUrl("https://example.com/license-updated.pdf");

        when(driverDocumentService.updateDocument(eq(10), any(CreateDriverDocumentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/driver-documents/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentUrl").value("https://example.com/license-updated.pdf"));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should return 403 Forbidden when updating document as SCHOOL_ADMIN")
    void updateDocument_forbidden_asSchoolAdmin() throws Exception {
        CreateDriverDocumentRequest request = new CreateDriverDocumentRequest();
        request.setDriverId(1);
        request.setDocumentType("LICENSE");
        request.setDocumentUrl("https://example.com/license.pdf");

        mockMvc.perform(put("/driver-documents/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("Should get all driver documents successfully when caller is SUPER_ADMIN")
    void getAllDocuments_success_asSuperAdmin() throws Exception {
        DriverDocumentResponse response = new DriverDocumentResponse();
        response.setId(10);
        response.setDocumentType("LICENSE");

        when(driverDocumentService.getAllDocuments()).thenReturn(List.of(response));

        mockMvc.perform(get("/driver-documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should get document by ID successfully when caller is SCHOOL_ADMIN")
    void getDocumentById_success_asSchoolAdmin() throws Exception {
        DriverDocumentResponse response = new DriverDocumentResponse();
        response.setId(10);
        response.setDocumentType("LICENSE");

        when(driverDocumentService.getDocumentById(10)).thenReturn(response);

        mockMvc.perform(get("/driver-documents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("Should return 403 Forbidden when unauthenticated")
    void getDocumentById_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(get("/driver-documents/10"))
                .andExpect(status().isForbidden());
    }
}

