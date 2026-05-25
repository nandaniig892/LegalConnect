package com.ruralhealthcare.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BulkSyncRequest(
    @NotEmpty @Valid List<HealthRecordSyncItem> records
) {}
