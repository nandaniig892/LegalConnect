package com.ruralhealthcare.controller;

import com.ruralhealthcare.service.impl.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "System administration — analytics, user management, doctor approvals")
public class AdminController {

    private AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get platform-wide dashboard statistics")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/doctors/pending")
    @Operation(summary = "List all doctors pending approval")
    public ResponseEntity<List<Map<String, Object>>> pendingDoctors() {
        return ResponseEntity.ok(adminService.getPendingDoctors());
    }

    @PutMapping("/approve-doctor/{id}")
    @Operation(summary = "Approve a doctor registration")
    public ResponseEntity<Map<String, Object>> approveDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveDoctor(id));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all system users for audit")
    public ResponseEntity<List<Map<String, Object>>> allUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}
