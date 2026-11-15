package com.tgs.transport_governance_api.users.service;

import com.tgs.transport_governance_api.users.entity.Role;
import com.tgs.transport_governance_api.users.entity.User;
import com.tgs.transport_governance_api.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("loadUserByUsername: returns UserDetails with ROLE_ authorities when user exists")
    void loadUserByUsername_success() {
        Role role = new Role();
        role.setName("SCHOOL_ADMIN");

        User user = new User();
        user.setEmail("principal@school.com");
        user.setPasswordHash("hashedPassword");
        user.setRoles(Set.of(role));

        when(userRepository.findByEmail("principal@school.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("principal@school.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("principal@school.com");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_SCHOOL_ADMIN");
    }

    @Test
    @DisplayName("loadUserByUsername: throws UsernameNotFoundException when user does not exist")
    void loadUserByUsername_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail("missing@tgs.com")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@tgs.com")
        );
    }
}

