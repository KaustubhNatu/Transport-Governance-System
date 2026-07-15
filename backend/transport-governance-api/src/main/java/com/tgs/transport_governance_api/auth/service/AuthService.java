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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RoleRepository roleRepository;

    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
            request.getPassword(),
            user.getPasswordHash())) {
                throw new RuntimeException("Invalid email or password");
            }

        String token =
                jwtService.generateToken(
                        user.getEmail());

        return new LoginResponse(token);
    }
    public UserResponse register(RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User already exists");
        }

        Role role = roleRepository
                .findByNameIgnoreCase("PARENT")
                .orElseThrow();


        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setStatus("ACTIVE");
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());

        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(java.util.stream.Collectors.toSet())
        );

        return response;
    }
}
