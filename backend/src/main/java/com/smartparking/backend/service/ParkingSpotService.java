package com.smartparking.backend.service;

import com.smartparking.backend.dto.SpotRequest;
import com.smartparking.backend.dto.SpotResponse;
import com.smartparking.backend.model.Parking;
import com.smartparking.backend.model.ParkingSpot;
import com.smartparking.backend.repository.ParkingRepository;
import com.smartparking.backend.repository.ParkingSpotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParkingSpotService {

    private final ParkingRepository parkingRepository;
    private final ParkingSpotRepository spotRepository;

    public ParkingSpotService(ParkingRepository parkingRepository,
                              ParkingSpotRepository spotRepository) {
        this.parkingRepository = parkingRepository;
        this.spotRepository = spotRepository;
    }

    public List<SpotResponse> listSpots(Long parkingId) {
        findParking(parkingId);
        LocalDateTime now = LocalDateTime.now();
        Set<Long> availableIds = spotRepository
                .findAvailable(parkingId, now, now.plusMinutes(1))
                .stream()
                .map(ParkingSpot::getId)
                .collect(Collectors.toSet());

        return spotRepository.findByParkingId(parkingId).stream()
                .map(s -> new SpotResponse(s.getId(), s.getNumber(), availableIds.contains(s.getId())))
                .toList();
    }

    public SpotResponse createSpot(Long parkingId, SpotRequest request) {
        Parking parking = findParking(parkingId);
        ParkingSpot spot = new ParkingSpot();
        spot.setNumber(request.number());
        spot.setParking(parking);
        ParkingSpot saved = spotRepository.save(spot);
        return new SpotResponse(saved.getId(), saved.getNumber(), true);
    }

    private Parking findParking(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking introuvable"));
    }
}