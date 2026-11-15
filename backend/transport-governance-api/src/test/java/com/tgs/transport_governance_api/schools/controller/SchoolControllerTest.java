package com.tgs.transport_governance_api.schools.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.schools.dto.CreateSchoolRequest;
import com.tgs.transport_governance_api.schools.dto.SchoolResponse;
import com.tgs.transport_governance_api.schools.service.SchoolService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SchoolController.class)
@Import({SchoolController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class SchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private SchoolService schoolService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /schools: creates school successfully when role is SCHOOL_ADMIN")
    void createSchool_success_asSchoolAdmin() throws Exception {
        CreateSchoolRequest request = new CreateSchoolRequest();
        request.setName("High School");

        SchoolResponse response = new SchoolResponse();
        response.setId(1);
        response.setName("High School");

        when(schoolService.createSchool(any(CreateSchoolRequest.class))).thenReturn(response);

        mockMvc.perform(post("/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("High School"));

        verify(schoolService).createSchool(any(CreateSchoolRequest.class));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /schools: returns 403 Forbidden for VENDOR_ADMIN")
    void createSchool_forbidden_forVendorAdmin() throws Exception {
        CreateSchoolRequest request = new CreateSchoolRequest();
        request.setName("High School");

        mockMvc.perform(post("/schools")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /schools: returns all schools")
    void getAllSchools_success() throws Exception {
        SchoolResponse response = new SchoolResponse();
        response.setId(1);
        response.setName("School 1");

        when(schoolService.getAllSchools()).thenReturn(List.of(response));

        mockMvc.perform(get("/schools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("School 1"));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /schools/{id}: returns 404 Not Found when school does not exist")
    void getSchoolById_notFound() throws Exception {
        when(schoolService.getSchoolById(99)).thenThrow(new ResourceNotFoundException("School not found"));

        mockMvc.perform(get("/schools/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("DELETE /schools/{id}: allows SUPER_ADMIN to delete school")
    void deleteSchool_success_asSuperAdmin() throws Exception {
        mockMvc.perform(delete("/schools/1"))
                .andExpect(status().isOk());

        verify(schoolService).deleteSchool(1);
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("DELETE /schools/{id}: returns 403 Forbidden for VENDOR_ADMIN")
    void deleteSchool_forbidden_asVendorAdmin() throws Exception {
        mockMvc.perform(delete("/schools/1"))
                .andExpect(status().isForbidden());
    }
}

