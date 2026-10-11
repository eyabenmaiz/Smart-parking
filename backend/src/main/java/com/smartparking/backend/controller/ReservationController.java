package com.smartparking.backend.controller;

import com.smartparking.backend.dto.ReservationRequest;
import com.smartparking.backend.dto.ReservationResponse;
import com.smartparking.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@AuthenticationPrincipal Jwt jwt,
                                      @Valid @RequestBody ReservationRequest request) {
        return reservationService.create(jwt.getSubject(), request);
    }

    @GetMapping("/me")
    public List<ReservationResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return reservationService.mine(jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        reservationService.cancel(jwt.getSubject(), id);
    }
}