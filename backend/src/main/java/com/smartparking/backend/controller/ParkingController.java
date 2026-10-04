package com.smartparking.backend.controller;

import com.smartparking.backend.dto.ParkingRequest;
import com.smartparking.backend.dto.ParkingResponse;
import com.smartparking.backend.dto.SpotRequest;
import com.smartparking.backend.dto.SpotResponse;
import com.smartparking.backend.service.ParkingService;
import com.smartparking.backend.service.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingService parkingService;
    private final ParkingSpotService spotService;

    public ParkingController(ParkingService parkingService, ParkingSpotService spotService) {
        this.parkingService = parkingService;
        this.spotService = spotService;
    }


    @GetMapping
    public List<ParkingResponse> list() {
        return parkingService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingResponse create(@Valid @RequestBody ParkingRequest request) {
        return parkingService.create(request);
    }
    @GetMapping("/{id}")
    public ParkingResponse get(@PathVariable Long id) {
        return parkingService.findById(id);
    }

    @GetMapping("/{id}/spots")
    public List<SpotResponse> spots(@PathVariable Long id) {
        return spotService.listSpots(id);
    }

    @PostMapping("/{id}/spots")
    @ResponseStatus(HttpStatus.CREATED)
    public SpotResponse addSpot(@PathVariable Long id, @Valid @RequestBody SpotRequest request) {
        return spotService.createSpot(id, request);
    }
}