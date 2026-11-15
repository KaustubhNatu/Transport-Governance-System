package com.tgs.transport_governance_api.drivers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.drivers.dto.CreateDriverRequest;
import com.tgs.transport_governance_api.drivers.dto.DriverResponse;
import com.tgs.transport_governance_api.drivers.service.DriverService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
@Import({DriverController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /drivers: allows VENDOR_ADMIN to create driver")
    void createDriver_success_asVendorAdmin() throws Exception {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setUserId(1);
        request.setVendorId(2);
        request.setLicenseNumber("DL-100");

        DriverResponse response = new DriverResponse();
        response.setId(1);
        response.setLicenseNumber("DL-100");

        when(driverService.createDriver(any(CreateDriverRequest.class))).thenReturn(response);

        mockMvc.perform(post("/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.licenseNumber").value("DL-100"));

        verify(driverService).createDriver(any(CreateDriverRequest.class));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /drivers: returns 403 Forbidden for SCHOOL_ADMIN")
    void createDriver_forbidden_asSchoolAdmin() throws Exception {
        CreateDriverRequest request = new CreateDriverRequest();
        request.setLicenseNumber("DL-100");

        mockMvc.perform(post("/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /drivers: allows SCHOOL_ADMIN to view drivers")
    void getAllDrivers_success_asSchoolAdmin() throws Exception {
        DriverResponse response = new DriverResponse();
        response.setId(1);
        response.setLicenseNumber("DL-100");

        when(driverService.getAllDrivers()).thenReturn(List.of(response));

        mockMvc.perform(get("/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].licenseNumber").value("DL-100"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("GET /drivers: returns 403 Forbidden for PARENT")
    void getAllDrivers_forbidden_asParent() throws Exception {
        mockMvc.perform(get("/drivers"))
                .andExpect(status().isForbidden());
    }
}

