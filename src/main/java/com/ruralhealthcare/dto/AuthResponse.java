package com.ruralhealthcare.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String email,
    String fullName,
    String role
) {}
