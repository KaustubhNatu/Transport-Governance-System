package com.tgs.transport_governance_api.auth;

import com.tgs.transport_governance_api.users.entity.Role;
import com.tgs.transport_governance_api.users.entity.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    private User createTestUser(String email, String roleName) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");

        Role role = new Role();
        role.setId(1);
        role.setName(roleName);
        user.setRoles(Set.of(role));

        return user;
    }

    @Test
    @DisplayName("generateToken: should generate a non-empty token and extract matching username")
    void generateToken_and_extractUsername() {
        User user = createTestUser("admin@tgs.com", "SUPER_ADMIN");

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        String extractedEmail = jwtService.extractUsername(token);
        assertThat(extractedEmail).isEqualTo("admin@tgs.com");
    }

    @Test
    @DisplayName("extractExpiration: should extract an expiration date in the future")
    void extractExpiration_isFutureDate() {
        User user = createTestUser("parent@tgs.com", "PARENT");

        String token = jwtService.generateToken(user);
        Date expiration = jwtService.extractExpiration(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("isTokenValid: returns true when username matches and token is not expired")
    void isTokenValid_returnsTrue_whenMatchingUser() {
        User user = createTestUser("school@tgs.com", "SCHOOL_ADMIN");
        String token = jwtService.generateToken(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("school@tgs.com");

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid: returns false when username does not match token subject")
    void isTokenValid_returnsFalse_whenUsernameMismatch() {
        User user = createTestUser("school@tgs.com", "SCHOOL_ADMIN");
        String token = jwtService.generateToken(user);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("other@tgs.com");

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("extractUsername: throws JwtException when token is tampered or malformed")
    void extractUsername_throwsException_whenTamperedToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalidPayload.invalidSignature";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken));
    }
}

