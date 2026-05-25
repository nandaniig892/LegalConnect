package com.ruralhealthcare.controller;

import com.ruralhealthcare.dto.BulkSyncRequest;
import com.ruralhealthcare.service.impl.HealthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health-records")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Health Records", description = "Offline health record synchronization and retrieval")
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    // Manual Constructor Injection
    public HealthRecordController(HealthRecordService healthRecordService) {
        this.healthRecordService = healthRecordService;
    }

    @PostMapping("/sync")
    @Operation(summary = "Bulk sync offline health records (PATIENT) — Last-Write-Wins conflict resolution")
    public ResponseEntity<Map<String, Object>> syncRecords(
            @Valid @RequestBody BulkSyncRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(healthRecordService.syncRecords(request, userDetails.getUsername()));
    }

    @GetMapping("/me")
    @Operation(summary = "Get authenticated patient's health records sorted by date descending")
    public ResponseEntity<List<Map<String, Object>>> getMyRecords(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(healthRecordService.getPatientRecords(userDetails.getUsername()));
    }
}