package com.ruralhealthcare.controller;

import com.ruralhealthcare.dto.SlotRequest;
import com.ruralhealthcare.service.impl.DoctorSlotService;
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
@RequestMapping("/api/v1/doctors")
@Tag(name = "Doctor Slots", description = "Doctor availability slot management")
public class DoctorController {

    private final DoctorSlotService slotService;

    // Manual Constructor for Spring Bean Factory (Eclipse & Compiler Friendly)
    public DoctorController(DoctorSlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping("/slots")
    @Operation(summary = "Get all available doctor slots (PUBLIC)")
    public ResponseEntity<List<Map<String, Object>>> getAvailableSlots() {
        return ResponseEntity.ok(slotService.getAvailableSlots());
    }

    @PostMapping("/slots")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new availability slot (DOCTOR)")
    public ResponseEntity<Map<String, Object>> createSlot(
            @Valid @RequestBody SlotRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.createSlot(request, userDetails.getUsername()));
    }

    @DeleteMapping("/slots/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete an availability slot (DOCTOR)")
    public ResponseEntity<Map<String, String>> deleteSlot(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        slotService.deleteSlot(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Slot deleted"));
    }
}