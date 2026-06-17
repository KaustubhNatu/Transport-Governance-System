package com.tgs.transport_governance_api.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email
    @NotBlank(message = "Email is required")
    @Pattern(regexp ="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message ="Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters and contain letters and numbers")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = ("Phone number required"))
    private String phone;
}
