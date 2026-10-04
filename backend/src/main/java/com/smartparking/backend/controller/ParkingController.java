package com.smartparking.backend.controller;

import com.smartparking.backend.dto.ParkingRequest;
import com.smartparking.backend.dto.ParkingResponse;
import com.smartparking.backend.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parkings")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
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
}