package com.tgs.transport_governance_api.parents.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.parents.dto.CreateParentRequest;
import com.tgs.transport_governance_api.parents.dto.ParentResponse;
import com.tgs.transport_governance_api.parents.service.ParentService;
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

@WebMvcTest(ParentController.class)
@Import({ParentController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class ParentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ParentService parentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /parents: allows SCHOOL_ADMIN to create parent")
    void createParent_success_asSchoolAdmin() throws Exception {
        CreateParentRequest request = new CreateParentRequest();
        request.setUserId(10);
        request.setAddress("123 Street");

        ParentResponse response = new ParentResponse();
        response.setId(1);
        response.setUserId(10);

        when(parentService.createParent(any(CreateParentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10));

        verify(parentService).createParent(any(CreateParentRequest.class));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /parents: returns 403 Forbidden for VENDOR_ADMIN")
    void createParent_forbidden_asVendorAdmin() throws Exception {
        CreateParentRequest request = new CreateParentRequest();
        request.setUserId(10);

        mockMvc.perform(post("/parents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("GET /parents: allows SUPER_ADMIN to view parents")
    void getAllParents_success_asSuperAdmin() throws Exception {
        ParentResponse response = new ParentResponse();
        response.setId(1);
        response.setUserId(10);

        when(parentService.getAllParents()).thenReturn(List.of(response));

        mockMvc.perform(get("/parents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(10));
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("GET /parents: returns 403 Forbidden for VENDOR_ADMIN")
    void getAllParents_forbidden_asVendorAdmin() throws Exception {
        mockMvc.perform(get("/parents"))
                .andExpect(status().isForbidden());
    }
}

