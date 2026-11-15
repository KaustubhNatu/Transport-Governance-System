package com.tgs.transport_governance_api.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.auth.dto.LoginRequest;
import com.tgs.transport_governance_api.auth.dto.LoginResponse;
import com.tgs.transport_governance_api.auth.dto.RegisterRequest;
import com.tgs.transport_governance_api.auth.service.AuthService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.users.dto.UserResponse;
import com.tgs.transport_governance_api.users.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({AuthController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /auth/login: permits unauthenticated access and returns token on valid credentials")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@tgs.com");
        request.setPassword("password123");

        LoginResponse response = new LoginResponse("sample.jwt.token");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample.jwt.token"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /auth/login: returns 400 Bad Request when validation fails on email or password")
    void login_failsValidation_whenFieldsBlank() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /auth/login: returns 400 Bad Request when authService throws invalid credentials exception")
    void login_returnsBadRequest_whenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@tgs.com");
        request.setPassword("wrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /auth/register: permits unauthenticated access and registers new user")
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@tgs.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("1234567890");

        UserResponse response = new UserResponse();
        response.setId(1);
        response.setEmail("newuser@tgs.com");
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setStatus("ACTIVE");
        response.setRoles(Set.of("PARENT"));

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("newuser@tgs.com"))
                .andExpect(jsonPath("$.roles[0]").value("PARENT"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /auth/register: returns 400 Bad Request when user already exists")
    void register_returnsBadRequest_whenDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@tgs.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhone("1234567890");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BadRequestException("User already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("User already exists"));
    }
}

