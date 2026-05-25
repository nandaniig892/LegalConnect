package com.ruralhealthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentRequest(
    @NotNull Long slotId,
    String patientNotes
) {}
