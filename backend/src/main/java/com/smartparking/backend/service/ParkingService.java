package com.smartparking.backend.service;

import com.smartparking.backend.dto.ParkingRequest;
import com.smartparking.backend.dto.ParkingResponse;
import com.smartparking.backend.model.Parking;
import com.smartparking.backend.repository.ParkingRepository;
import com.smartparking.backend.repository.ParkingSpotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingSpotRepository spotRepository;

    public ParkingService(ParkingRepository parkingRepository,
                          ParkingSpotRepository spotRepository) {
        this.parkingRepository = parkingRepository;
        this.spotRepository = spotRepository;
    }

    public List<ParkingResponse> findAll() {
        LocalDateTime now = LocalDateTime.now();
        return parkingRepository.findAll().stream()
                .map(p -> toResponse(p, now))
                .toList();
    }

    public ParkingResponse findById(Long id) {
        return parkingRepository.findById(id)
                .map(p -> toResponse(p, LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking introuvable"));
    }

    public ParkingResponse create(ParkingRequest request) {
        Parking parking = new Parking();
        parking.setName(request.name());
        parking.setAddress(request.address());
        parking.setCapacity(request.capacity());
        return toResponse(parkingRepository.save(parking), LocalDateTime.now());
    }

    private ParkingResponse toResponse(Parking p, LocalDateTime now) {
        int available = (int) spotRepository.countAvailable(p.getId(), now, now.plusMinutes(1));
        return new ParkingResponse(p.getId(), p.getName(), p.getAddress(), p.getCapacity(), available);
    }
}