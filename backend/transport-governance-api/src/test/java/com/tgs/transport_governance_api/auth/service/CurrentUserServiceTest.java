package com.tgs.transport_governance_api.auth.service;

import com.tgs.transport_governance_api.users.entity.User;
import com.tgs.transport_governance_api.users.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserService currentUserService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getCurrentUser: returns User entity matching SecurityContext authentication")
    void getCurrentUser_success() {
        String email = "current@tgs.com";
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setId(5);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = currentUserService.getCurrentUser();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5);
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("getCurrentUser: throws RuntimeException when authenticated user not found in repository")
    void getCurrentUser_throwsRuntimeException_whenUserNotFound() {
        String email = "missing@tgs.com";
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> currentUserService.getCurrentUser()
        );

        assertThat(exception.getMessage()).isEqualTo("Authenticated user not found");
    }
}

