package com.tgs.transport_governance_api.vehicledocuments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.users.service.CustomUserDetailsService;
import com.tgs.transport_governance_api.vehicledocuments.dto.CreateVehicleDocumentRequest;
import com.tgs.transport_governance_api.vehicledocuments.dto.VehicleDocumentResponse;
import com.tgs.transport_governance_api.vehicledocuments.service.VehicleDocumentService;
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

@WebMvcTest(VehicleDocumentController.class)
@Import({VehicleDocumentController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class VehicleDocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleDocumentService vehicleDocumentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should create vehicle document successfully when caller is VENDOR_ADMIN")
    void createDocument_success_asVendorAdmin() throws Exception {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("INSURANCE");
        request.setDocumentUrl("https://example.com/insurance.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(1));

        VehicleDocumentResponse response = new VehicleDocumentResponse();
        response.setId(10);
        response.setVehicleId(1);
        response.setDocumentType("INSURANCE");
        response.setDocumentUrl("https://example.com/insurance.pdf");
        response.setExpiryDate(request.getExpiryDate());
        response.setUploadedAt(LocalDateTime.now());

        when(vehicleDocumentService.createDocument(any(CreateVehicleDocumentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/vehicle-documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.documentType").value("INSURANCE"));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should return 400 Bad Request when validation fails on vehicle document creation")
    void createDocument_validationFailure_missingFields() throws Exception {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();

        mockMvc.perform(post("/vehicle-documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should return 403 Forbidden when creating vehicle document as SCHOOL_ADMIN")
    void createDocument_forbidden_asSchoolAdmin() throws Exception {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("INSURANCE");
        request.setDocumentUrl("https://example.com/insurance.pdf");

        mockMvc.perform(post("/vehicle-documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("Should update vehicle document successfully when caller is VENDOR_ADMIN")
    void updateDocument_success_asVendorAdmin() throws Exception {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("INSURANCE");
        request.setDocumentUrl("https://example.com/insurance-updated.pdf");
        request.setExpiryDate(LocalDate.now().plusYears(2));

        VehicleDocumentResponse response = new VehicleDocumentResponse();
        response.setId(10);
        response.setVehicleId(1);
        response.setDocumentType("INSURANCE");
        response.setDocumentUrl("https://example.com/insurance-updated.pdf");

        when(vehicleDocumentService.updateDocument(eq(10), any(CreateVehicleDocumentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/vehicle-documents/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentUrl").value("https://example.com/insurance-updated.pdf"));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should return 403 Forbidden when updating vehicle document as SCHOOL_ADMIN")
    void updateDocument_forbidden_asSchoolAdmin() throws Exception {
        CreateVehicleDocumentRequest request = new CreateVehicleDocumentRequest();
        request.setVehicleId(1);
        request.setDocumentType("INSURANCE");
        request.setDocumentUrl("https://example.com/insurance.pdf");

        mockMvc.perform(put("/vehicle-documents/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("Should get all vehicle documents successfully when caller is SUPER_ADMIN")
    void getAllDocuments_success_asSuperAdmin() throws Exception {
        VehicleDocumentResponse response = new VehicleDocumentResponse();
        response.setId(10);
        response.setDocumentType("INSURANCE");

        when(vehicleDocumentService.getAllDocuments()).thenReturn(List.of(response));

        mockMvc.perform(get("/vehicle-documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("Should get vehicle document by ID successfully when caller is SCHOOL_ADMIN")
    void getDocumentById_success_asSchoolAdmin() throws Exception {
        VehicleDocumentResponse response = new VehicleDocumentResponse();
        response.setId(10);
        response.setDocumentType("INSURANCE");

        when(vehicleDocumentService.getDocumentById(10)).thenReturn(response);

        mockMvc.perform(get("/vehicle-documents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("Should return 403 Forbidden when unauthenticated")
    void getDocumentById_unauthenticated_forbidden() throws Exception {
        mockMvc.perform(get("/vehicle-documents/10"))
                .andExpect(status().isForbidden());
    }
}
