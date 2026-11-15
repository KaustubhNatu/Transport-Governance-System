package com.tgs.transport_governance_api.routes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.routes.dto.CreateRouteRequest;
import com.tgs.transport_governance_api.routes.dto.RouteResponse;
import com.tgs.transport_governance_api.routes.service.RouteService;
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

@WebMvcTest(RouteController.class)
@Import({RouteController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private RouteService routeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("POST /routes: allows VENDOR_ADMIN to create route")
    void createRoute_success_asVendorAdmin() throws Exception {
        CreateRouteRequest request = new CreateRouteRequest();
        request.setVendorId(1);
        request.setRouteName("Route 1");

        RouteResponse response = new RouteResponse();
        response.setId(1);
        response.setRouteName("Route 1");

        when(routeService.createRoute(any(CreateRouteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.routeName").value("Route 1"));

        verify(routeService).createRoute(any(CreateRouteRequest.class));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("POST /routes: returns 403 Forbidden for PARENT")
    void createRoute_forbidden_asParent() throws Exception {
        CreateRouteRequest request = new CreateRouteRequest();
        request.setRouteName("Route 1");

        mockMvc.perform(post("/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("GET /routes: allows SCHOOL_ADMIN to view routes")
    void getAllRoutes_success_asSchoolAdmin() throws Exception {
        RouteResponse response = new RouteResponse();
        response.setId(1);
        response.setRouteName("Route 1");

        when(routeService.getAllRoutes()).thenReturn(List.of(response));

        mockMvc.perform(get("/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeName").value("Route 1"));
    }

    @Test
    @WithMockUser(roles = "PARENT")
    @DisplayName("GET /routes: returns 403 Forbidden for PARENT")
    void getAllRoutes_forbidden_asParent() throws Exception {
        mockMvc.perform(get("/routes"))
                .andExpect(status().isForbidden());
    }
}

