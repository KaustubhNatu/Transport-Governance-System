package com.tgs.transport_governance_api.auth.controller;

import com.tgs.transport_governance_api.auth.dto.LoginRequest;
import com.tgs.transport_governance_api.auth.dto.LoginResponse;
import com.tgs.transport_governance_api.auth.dto.RegisterRequest;
import com.tgs.transport_governance_api.auth.service.AuthService;
import com.tgs.transport_governance_api.users.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {

        return authService.login(request);
    }

    @PostMapping("/register")
    public UserResponse register(
            @RequestBody @Valid RegisterRequest request) {

        return authService.register(request);
    }

}