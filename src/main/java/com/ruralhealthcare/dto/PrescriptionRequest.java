package com.ruralhealthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrescriptionRequest(
    @NotNull Long appointmentId,
    @NotBlank String medicineList,
    String instructions,
    String diagnosis,
    String followUpDate
) {}
