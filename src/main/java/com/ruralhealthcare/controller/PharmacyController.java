package com.ruralhealthcare.controller;

import com.ruralhealthcare.dto.InventoryRequest;
import com.ruralhealthcare.service.impl.PharmacyService;
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
@RequestMapping("/api/v1/pharmacy")
@Tag(name = "Pharmacy", description = "Medicine inventory tracking and pharmacy proximity search")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    // Manual Constructor Injection
    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/search")
    @Operation(summary = "Find nearby pharmacies with live medicine availability (PUBLIC)")
    public ResponseEntity<List<Map<String, Object>>> searchNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double radiusKm,
            @RequestParam(required = false) String medicine
    ) {
        return ResponseEntity.ok(pharmacyService.searchNearby(lat, lng, radiusKm, medicine));
    }

    @PostMapping("/inventory")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add a medicine to pharmacy inventory (PHARMACY)")
    public ResponseEntity<Map<String, Object>> addInventory(
            @Valid @RequestBody InventoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pharmacyService.upsertInventory(request, userDetails.getUsername()));
    }

    @PutMapping("/inventory/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update an existing inventory item (PHARMACY)")
    public ResponseEntity<Map<String, Object>> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(pharmacyService.updateInventory(id, request, userDetails.getUsername()));
    }
}