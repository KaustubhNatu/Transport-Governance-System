package com.tgs.transport_governance_api.auth.service;

import com.tgs.transport_governance_api.auth.JwtService;
import com.tgs.transport_governance_api.auth.dto.LoginRequest;
import com.tgs.transport_governance_api.auth.dto.LoginResponse;
import com.tgs.transport_governance_api.auth.dto.RegisterRequest;
import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.users.dto.UserResponse;
import com.tgs.transport_governance_api.users.entity.Role;
import com.tgs.transport_governance_api.users.entity.User;
import com.tgs.transport_governance_api.users.repository.RoleRepository;
import com.tgs.transport_governance_api.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("login: returns JWT token on valid credentials")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@tgs.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1);
        user.setEmail("user@tgs.com");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByEmail("user@tgs.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt.token.here");

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.token.here");
    }

    @Test
    @DisplayName("login: throws RuntimeException when user not found")
    void login_throwsException_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@tgs.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@tgs.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertThat(exception.getMessage()).isEqualTo("Invalid email or password");
    }

    @Test
    @DisplayName("login: throws RuntimeException when password does not match")
    void login_throwsException_whenPasswordMismatch() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@tgs.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("user@tgs.com");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByEmail("user@tgs.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertThat(exception.getMessage()).isEqualTo("Invalid email or password");
    }

    @Test
    @DisplayName("register: registers new parent user with encoded password and ACTIVE status")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newparent@tgs.com");
        request.setPassword("secret123");
        request.setFirstName("Parent");
        request.setLastName("One");
        request.setPhone("1234567890");

        Role role = new Role();
        role.setId(2);
        role.setName("PARENT");

        when(userRepository.findByEmail("newparent@tgs.com")).thenReturn(Optional.empty());
        when(roleRepository.findByNameIgnoreCase("PARENT")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret123")).thenReturn("hashedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(99);
            return u;
        });

        UserResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(99);
        assertThat(response.getEmail()).isEqualTo("newparent@tgs.com");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getRoles()).containsExactly("PARENT");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User captured = captor.getValue();
        assertThat(captured.getPasswordHash()).isEqualTo("hashedSecret");
        assertThat(captured.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("register: throws BadRequestException when email already exists")
    void register_throwsBadRequestException_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@tgs.com");

        when(userRepository.findByEmail("existing@tgs.com")).thenReturn(Optional.of(new User()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.register(request));
        assertThat(exception.getMessage()).isEqualTo("User already exists");
        verify(userRepository, never()).save(any(User.class));
    }
}

