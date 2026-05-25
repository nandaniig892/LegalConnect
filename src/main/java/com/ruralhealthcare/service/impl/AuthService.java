package com.ruralhealthcare.service.impl;

import com.ruralhealthcare.dto.*;
import com.ruralhealthcare.entity.RefreshToken;
import com.ruralhealthcare.entity.Role;
import com.ruralhealthcare.entity.User;
import com.ruralhealthcare.exception.ResourceNotFoundException;
import com.ruralhealthcare.repository.UserRepository;
import com.ruralhealthcare.security.JwtService;
import com.ruralhealthcare.security.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    // Manual Constructor for Dependency Injection (No Lombok needed)
    public AuthService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       JwtService jwtService, 
                       RefreshTokenService refreshTokenService, 
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        Role role = parseRole(request.role());

        // FIXED: Using standard setters instead of Lombok Builder
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setPhoneNumber(request.phoneNumber());
        user.setApproved(role != Role.ROLE_DOCTOR);

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Transactional
    public AuthResponse refreshToken(String rawToken) {
        RefreshToken newRefreshToken = refreshTokenService.verifyAndRotate(rawToken);
        User user = newRefreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponse(accessToken, newRefreshToken.getToken(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        refreshTokenService.revokeAll(user);
    }

    private Role parseRole(String roleName) {
        try {
            String normalized = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName.toUpperCase();
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleName);
        }
    }
}