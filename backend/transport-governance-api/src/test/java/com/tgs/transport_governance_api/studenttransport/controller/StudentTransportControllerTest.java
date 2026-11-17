package com.tgs.transport_governance_api.studenttransport.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.studenttransport.dto.CreateStudentTransportRequest;
import com.tgs.transport_governance_api.studenttransport.dto.StudentTransportResponse;
import com.tgs.transport_governance_api.studenttransport.service.StudentTransportService;
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

@WebMvcTest(StudentTransportController.class)
@Import({StudentTransportController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class StudentTransportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private StudentTransportService studentTransportService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /student-transport: allows SCHOOL_ADMIN to assign transport")
    void createStudentTransport_success_asSchoolAdmin() throws Exception {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);

        StudentTransportResponse response = new StudentTransportResponse();
        response.setId(10);
        response.setStudentId(1);
        response.setVendorId(2);
        response.setStatus("ACTIVE");

        when(studentTransportService.createStudentTransport(any(CreateStudentTransportRequest.class))).thenReturn(response);

        mockMvc.perform(post("/student-transport")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(studentTransportService).createStudentTransport(any(CreateStudentTransportRequest.class));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /student-transport: returns 403 Forbidden for VENDOR_ADMIN")
    void createStudentTransport_forbidden_asVendorAdmin() throws Exception {
        CreateStudentTransportRequest request = new CreateStudentTransportRequest();
        request.setStudentId(1);
        request.setVendorId(2);

        mockMvc.perform(post("/student-transport")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /student-transport: allows SCHOOL_ADMIN to list assignments")
    void getAllStudentTransport_success_asSchoolAdmin() throws Exception {
        StudentTransportResponse response = new StudentTransportResponse();
        response.setId(10);
        response.setStatus("ACTIVE");

        when(studentTransportService.getAllStudentTransport()).thenReturn(List.of(response));

        mockMvc.perform(get("/student-transport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("GET /student-transport: returns 403 Forbidden for PARENT")
    void getAllStudentTransport_forbidden_asParent() throws Exception {
        mockMvc.perform(get("/student-transport"))
                .andExpect(status().isForbidden());
    }
}

