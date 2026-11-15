package com.tgs.transport_governance_api.vehicles.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.users.service.CustomUserDetailsService;
import com.tgs.transport_governance_api.vehicles.dto.CreateVehicleRequest;
import com.tgs.transport_governance_api.vehicles.dto.VehicleResponse;
import com.tgs.transport_governance_api.vehicles.service.VehicleService;
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

@WebMvcTest(VehicleController.class)
@Import({VehicleController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /vehicles: allows VENDOR_ADMIN to create vehicle")
    void createVehicle_success_asVendorAdmin() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setVendorId(1);
        request.setRegistrationNumber("REG-123");

        VehicleResponse response = new VehicleResponse();
        response.setId(1);
        response.setRegistrationNumber("REG-123");

        when(vehicleService.createVehicle(any(CreateVehicleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.registrationNumber").value("REG-123"));

        verify(vehicleService).createVehicle(any(CreateVehicleRequest.class));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("POST /vehicles: returns 403 Forbidden for PARENT")
    void createVehicle_forbidden_asParent() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setRegistrationNumber("REG-123");

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /vehicles: allows SCHOOL_ADMIN to view vehicles")
    void getAllVehicles_success_asSchoolAdmin() throws Exception {
        VehicleResponse response = new VehicleResponse();
        response.setId(1);
        response.setRegistrationNumber("REG-123");

        when(vehicleService.getAllVehicles()).thenReturn(List.of(response));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registrationNumber").value("REG-123"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("GET /vehicles: returns 403 Forbidden for PARENT")
    void getAllVehicles_forbidden_asParent() throws Exception {
        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isForbidden());
    }
}

