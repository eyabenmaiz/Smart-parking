package com.smartparking.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ParkingRequest(
        @NotBlank String name,
        @NotBlank String address,
        @Min(1) int capacity
) {}