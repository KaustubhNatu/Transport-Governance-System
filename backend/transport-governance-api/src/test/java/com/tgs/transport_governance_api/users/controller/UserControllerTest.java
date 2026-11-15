package com.tgs.transport_governance_api.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgs.transport_governance_api.auth.JwtAuthenticationFilter;
import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.common.exception.GlobalExceptionHandler;
import com.tgs.transport_governance_api.config.SecurityConfig;
import com.tgs.transport_governance_api.users.dto.CreateUserRequest;
import com.tgs.transport_governance_api.users.dto.UserResponse;
import com.tgs.transport_governance_api.users.service.CustomUserDetailsService;
import com.tgs.transport_governance_api.users.service.UserService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({UserController.class, SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("POST /users: creates user successfully when role is SUPER_ADMIN")
    void createUser_success_asSuperAdmin() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("newadmin@tgs.com");
        request.setPassword("password123");
        request.setFirstName("Admin");
        request.setLastName("User");
        request.setPhone("9876543210");
        request.setRoleName("SCHOOL_ADMIN");

        UserResponse response = new UserResponse();
        response.setId(1);
        response.setEmail("newadmin@tgs.com");
        response.setRoles(Set.of("SCHOOL_ADMIN"));

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("newadmin@tgs.com"));

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser(roles = "SCHOOL_ADMIN")
    @DisplayName("POST /users: returns 403 Forbidden when role is SCHOOL_ADMIN")
    void createUser_forbidden_asSchoolAdmin() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("newadmin@tgs.com");
        request.setPassword("password");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /users: returns 403 Forbidden when unauthenticated")
    void createUser_forbidden_whenUnauthenticated() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("newadmin@tgs.com");
        request.setPassword("password");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("GET /users: returns user list when role is SUPER_ADMIN")
    void getAllUsers_success_asSuperAdmin() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1);
        response.setEmail("super@tgs.com");

        when(userService.getAllUsers()).thenReturn(List.of(response));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("super@tgs.com"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "VENDOR_ADMIN")
    @DisplayName("GET /users: returns 403 Forbidden when role is VENDOR_ADMIN")
    void getAllUsers_forbidden_asVendorAdmin() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }
}

