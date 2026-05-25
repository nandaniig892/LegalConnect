package com.ruralhealthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(
    @NotBlank String medicineName,
    String genericName,
    String manufacturer,
    @NotNull Integer quantity,
    Double unitPrice
) {}
