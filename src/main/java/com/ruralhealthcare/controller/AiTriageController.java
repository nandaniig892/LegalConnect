package com.ruralhealthcare.controller;

import com.ruralhealthcare.dto.SymptomCheckRequest;
import com.ruralhealthcare.service.impl.SymptomTriageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AI Triage", description = "Rule-based symptom checker and medical triage module")
public class AiTriageController {

    private final SymptomTriageService triageService;

    // FIXED: Manual Constructor for Spring Dependency Injection (No Lombok needed)
    public AiTriageController(SymptomTriageService triageService) {
        this.triageService = triageService;
    }

    @PostMapping("/symptom-check")
    @Operation(
        summary = "Submit symptoms for AI triage evaluation (PATIENT)",
        description = "Accepts a structured list of symptoms with severity scores and duration. " +
                      "Returns urgency classification, actionable recommendations, and mandatory legal disclaimer."
    )
    public ResponseEntity<Map<String, Object>> checkSymptoms(@Valid @RequestBody SymptomCheckRequest request) {
        return ResponseEntity.ok(triageService.evaluate(request));
    }
}