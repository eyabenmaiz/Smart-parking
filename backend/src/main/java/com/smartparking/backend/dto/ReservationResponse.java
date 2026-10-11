package com.smartparking.backend.dto;

import com.smartparking.backend.model.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long spotId,
        String spotNumber,
        String parkingName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status
) {}