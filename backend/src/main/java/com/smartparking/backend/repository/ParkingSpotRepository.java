package com.smartparking.backend.repository;

import com.smartparking.backend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    List<ParkingSpot> findByParkingId(Long parkingId);

    @Query("""
        select s from ParkingSpot s
        where s.parking.id = :parkingId
          and s.outOfService = false
          and not exists (
              select r from Reservation r
              where r.parkingSpot = s
                and r.status = com.smartparking.backend.model.ReservationStatus.ACTIVATE
                and r.startTime < :end
                and r.endTime > :start
          )
        """)
    List<ParkingSpot> findAvailable(@Param("parkingId") Long parkingId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}