package com.ruralhealthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record HealthRecordSyncItem(
    @NotBlank String syncTokenHash,
    @NotBlank String metadataPayload,
    @NotNull LocalDateTime recordTimestamp,
    String diagnosisSummary
) {}
