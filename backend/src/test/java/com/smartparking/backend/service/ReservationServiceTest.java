package com.smartparking.backend.service;

import com.smartparking.backend.dto.ReservationRequest;
import com.smartparking.backend.dto.ReservationResponse;
import com.smartparking.backend.model.Parking;
import com.smartparking.backend.model.ParkingSpot;
import com.smartparking.backend.model.Reservation;
import com.smartparking.backend.model.ReservationStatus;
import com.smartparking.backend.model.User;
import com.smartparking.backend.repository.ParkingSpotRepository;
import com.smartparking.backend.repository.ReservationRepository;
import com.smartparking.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ParkingSpotRepository spotRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService service;

    private User user;
    private ParkingSpot spot;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("eya@test.com");

        Parking parking = new Parking();
        parking.setName("Parking Centre");

        spot = new ParkingSpot();
        spot.setId(1L);
        spot.setNumber("A1");
        spot.setParking(parking);

        start = LocalDateTime.now().plusDays(1);
        end = start.plusHours(2);
    }

    @Test
    void create_succeedsWhenSlotIsFree() {
        when(userRepository.findByEmail("eya@test.com")).thenReturn(Optional.of(user));
        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(reservationRepository.existsOverlap(anyLong(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArgument(0));

        ReservationResponse response =
                service.create("eya@test.com", new ReservationRequest(1L, start, end));

        assertEquals(ReservationStatus.ACTIVATE, response.status());
        assertEquals("A1", response.spotNumber());
    }

    @Test
    void create_failsWith409WhenSlotOverlaps() {
        when(userRepository.findByEmail("eya@test.com")).thenReturn(Optional.of(user));
        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(reservationRepository.existsOverlap(anyLong(), any(), any())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create("eya@test.com", new ReservationRequest(1L, start, end)));

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void create_failsWith400WhenEndIsBeforeStart() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create("eya@test.com", new ReservationRequest(1L, end, start)));

        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void cancel_setsStatusToCancelledForOwner() {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.ACTIVATE);
        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        service.cancel("eya@test.com", 10L);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void cancel_failsWith403ForAnotherUser() {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.ACTIVATE);
        when(reservationRepository.findById(10L)).thenReturn(Optional.of(reservation));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancel("autre@test.com", 10L));

        assertEquals(403, ex.getStatusCode().value());
        assertEquals(ReservationStatus.ACTIVATE, reservation.getStatus());
    }
}