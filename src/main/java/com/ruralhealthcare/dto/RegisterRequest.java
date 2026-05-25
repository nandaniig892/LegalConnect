package com.ruralhealthcare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 2, max = 80) String fullName,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String role,
    String phoneNumber
) {}
