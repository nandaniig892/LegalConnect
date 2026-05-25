package com.ruralhealthcare.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotRequest(
    @NotNull LocalDate slotDate,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    Integer maxPatients
) {}
