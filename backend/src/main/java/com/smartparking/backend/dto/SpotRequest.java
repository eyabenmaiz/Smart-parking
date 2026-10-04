package com.smartparking.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record SpotRequest(@NotBlank String number) {}