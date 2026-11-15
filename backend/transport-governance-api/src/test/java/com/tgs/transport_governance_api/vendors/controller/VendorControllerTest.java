package com.tgs.transport_governance_api.vendors.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.users.service.CustomUserDetailsService;
import com.tgs.transport_governance_api.vendors.dto.CreateVendorRequest;
import com.tgs.transport_governance_api.vendors.dto.VendorResponse;
import com.tgs.transport_governance_api.vendors.service.VendorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VendorController.class)
@Import({VendorController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private VendorService vendorService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("POST /vendors: allows SUPER_ADMIN to create vendor")
    void createVendor_success_asSuperAdmin() throws Exception {
        CreateVendorRequest request = new CreateVendorRequest();
        request.setSchoolId(1);
        request.setCompanyName("Express Co");

        VendorResponse response = new VendorResponse();
        response.setId(1);
        response.setCompanyName("Express Co");

        when(vendorService.createVendor(any(CreateVendorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("Express Co"));

        verify(vendorService).createVendor(any(CreateVendorRequest.class));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /vendors: returns 403 Forbidden for SCHOOL_ADMIN")
    void createVendor_forbidden_asSchoolAdmin() throws Exception {
        CreateVendorRequest request = new CreateVendorRequest();
        request.setSchoolId(1);
        request.setCompanyName("Express Co");

        mockMvc.perform(post("/vendors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("GET /vendors: allows VENDOR_ADMIN to view vendors")
    void getAllVendors_success_asVendorAdmin() throws Exception {
        VendorResponse response = new VendorResponse();
        response.setId(1);
        response.setCompanyName("Express Co");

        when(vendorService.getAllVendors()).thenReturn(List.of(response));

        mockMvc.perform(get("/vendors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Express Co"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("GET /vendors: returns 403 Forbidden for PARENT")
    void getAllVendors_forbidden_asParent() throws Exception {
        mockMvc.perform(get("/vendors"))
                .andExpect(status().isForbidden());
    }
}

