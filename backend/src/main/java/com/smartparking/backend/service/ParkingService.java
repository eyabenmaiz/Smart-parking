package com.smartparking.backend.service;

import com.smartparking.backend.dto.ParkingRequest;
import com.smartparking.backend.dto.ParkingResponse;
import com.smartparking.backend.model.Parking;
import com.smartparking.backend.repository.ParkingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    public List<ParkingResponse> findAll() {
        return parkingRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ParkingResponse create(ParkingRequest request) {
        Parking parking = new Parking();
        parking.setName(request.name());
        parking.setAddress(request.address());
        parking.setCapacity(request.capacity());
        return toResponse(parkingRepository.save(parking));
    }

    private ParkingResponse toResponse(Parking p) {
        return new ParkingResponse(p.getId(), p.getName(), p.getAddress(), p.getCapacity());
    }
}