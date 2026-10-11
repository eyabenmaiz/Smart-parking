package com.smartparking.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull Long spotId,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime
) {}
