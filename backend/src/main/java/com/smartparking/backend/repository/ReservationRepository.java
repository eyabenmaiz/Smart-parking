package com.smartparking.backend.repository;

import com.smartparking.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserEmailOrderByStartTimeDesc(String email);

    @Query("""
        select count(r) > 0 from Reservation r
        where r.parkingSpot.id = :spotId
          and r.status = com.smartparking.backend.model.ReservationStatus.ACTIVATE
          and r.startTime < :end
          and r.endTime > :start
        """)
    boolean existsOverlap(@Param("spotId") Long spotId,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);
}