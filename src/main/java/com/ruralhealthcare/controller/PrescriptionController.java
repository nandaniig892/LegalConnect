package com.ruralhealthcare.controller;

import com.ruralhealthcare.dto.PrescriptionRequest;
import com.ruralhealthcare.service.impl.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/prescriptions")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Prescriptions", description = "E-Prescription generation and retrieval")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // Manual Constructor Injection
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/write")
    @Operation(summary = "Write or update an E-Prescription (DOCTOR)")
    public ResponseEntity<Map<String, Object>> writePrescription(
            @Valid @RequestBody PrescriptionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prescriptionService.writePrescription(request, userDetails.getUsername()));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get prescription for a specific appointment")
    public ResponseEntity<Map<String, Object>> getPrescription(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionByAppointment(appointmentId));
    }
}