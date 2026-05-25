package com.ruralhealthcare.controller;

import com.ruralhealthcare.dto.AppointmentRequest;
import com.ruralhealthcare.service.impl.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Telemedicine appointment scheduling and management")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Manual Constructor for Eclipse & Spring Dependency Injection
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    @Operation(summary = "Book an available doctor slot (PATIENT)")
    public ResponseEntity<Map<String, Object>> book(
            @Valid @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.bookAppointment(request, userDetails.getUsername()));
    }

    @GetMapping("/my")
    @Operation(summary = "Get authenticated patient's appointments")
    public ResponseEntity<List<Map<String, Object>>> myAppointments(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(userDetails.getUsername()));
    }

    @GetMapping("/doctor")
    @Operation(summary = "Get all appointments for the authenticated doctor (DOCTOR)")
    public ResponseEntity<List<Map<String, Object>>> doctorAppointments(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(userDetails.getUsername()));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an appointment (PATIENT or DOCTOR)")
    public ResponseEntity<Map<String, String>> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        appointmentService.cancelAppointment(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Appointment cancelled successfully"));
    }
}