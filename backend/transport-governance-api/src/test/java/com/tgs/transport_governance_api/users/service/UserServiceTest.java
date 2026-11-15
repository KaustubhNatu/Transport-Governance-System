package com.tgs.transport_governance_api.users.service;

import com.tgs.transport_governance_api.exception.BadRequestException;
import com.tgs.transport_governance_api.exception.ResourceNotFoundException;
import com.tgs.transport_governance_api.users.dto.CreateUserRequest;
import com.tgs.transport_governance_api.users.dto.UserResponse;
import com.tgs.transport_governance_api.users.entity.Role;
import com.tgs.transport_governance_api.users.entity.User;
import com.tgs.transport_governance_api.users.repository.RoleRepository;
import com.tgs.transport_governance_api.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("createUser: successfully creates user with specified role and encoded password")
    void createUser_success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("schooladmin@tgs.com");
        request.setPassword("adminPass");
        request.setFirstName("School");
        request.setLastName("Admin");
        request.setPhone("9876543210");
        request.setRoleName("SCHOOL_ADMIN");

        Role role = new Role();
        role.setId(3);
        role.setName("SCHOOL_ADMIN");

        when(userRepository.findByEmail("schooladmin@tgs.com")).thenReturn(Optional.empty());
        when(roleRepository.findByNameIgnoreCase("SCHOOL_ADMIN")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("adminPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10);
            return u;
        });

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getEmail()).isEqualTo("schooladmin@tgs.com");
        assertThat(response.getRoles()).containsExactly("SCHOOL_ADMIN");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: throws BadRequestException when user already exists")
    void createUser_throwsBadRequestException_whenUserExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@tgs.com");

        when(userRepository.findByEmail("existing@tgs.com")).thenReturn(Optional.of(new User()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.createUser(request));
        assertThat(exception.getMessage()).isEqualTo("User already exists");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createUser: throws ResourceNotFoundException when roleName does not exist")
    void createUser_throwsResourceNotFoundException_whenRoleNotFound() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@tgs.com");
        request.setRoleName("NON_EXISTENT_ROLE");

        when(userRepository.findByEmail("new@tgs.com")).thenReturn(Optional.empty());
        when(roleRepository.findByNameIgnoreCase("NON_EXISTENT_ROLE")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.createUser(request));
        assertThat(exception.getMessage()).contains("Role not found");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getAllUsers: returns all mapped users")
    void getAllUsers_success() {
        Role role = new Role();
        role.setName("SUPER_ADMIN");

        User user = new User();
        user.setId(1);
        user.setEmail("super@tgs.com");
        user.setRoles(Set.of(role));

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> list = userService.getAllUsers();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getEmail()).isEqualTo("super@tgs.com");
    }
}

