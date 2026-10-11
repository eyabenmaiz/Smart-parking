package com.smartparking.backend.service;

import com.smartparking.backend.dto.ReservationRequest;
import com.smartparking.backend.dto.ReservationResponse;
import com.smartparking.backend.model.ParkingSpot;
import com.smartparking.backend.model.Reservation;
import com.smartparking.backend.model.ReservationStatus;
import com.smartparking.backend.model.User;
import com.smartparking.backend.repository.ParkingSpotRepository;
import com.smartparking.backend.repository.ReservationRepository;
import com.smartparking.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParkingSpotRepository spotRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ParkingSpotRepository spotRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.spotRepository = spotRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse create(String email, ReservationRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fin doit être après le début");
        }
        if (request.startTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le début est dans le passé");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
        ParkingSpot spot = spotRepository.findById(request.spotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Place introuvable"));
        if (spot.isOutOfService()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Place hors service");
        }
        if (reservationRepository.existsOverlap(spot.getId(), request.startTime(), request.endTime())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Place déjà réservée sur ce créneau");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setParkingSpot(spot);
        reservation.setStartTime(request.startTime());
        reservation.setEndTime(request.endTime());
        reservation.setStatus(ReservationStatus.ACTIVATE);
        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> mine(String email) {
        return reservationRepository.findByUserEmailOrderByStartTimeDesc(email).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void cancel(String email, Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation introuvable"));
        if (!reservation.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette réservation n'est pas la vôtre");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    private ReservationResponse toResponse(Reservation r) {
        ParkingSpot spot = r.getParkingSpot();
        return new ReservationResponse(
                r.getId(),
                spot.getId(),
                spot.getNumber(),
                spot.getParking().getName(),
                r.getStartTime(),
                r.getEndTime(),
                r.getStatus());
    }
}