package com.smartparking.backend.dto;

public record ParkingResponse(Long id, String name, String address, int capacity, int availableSpots) {}