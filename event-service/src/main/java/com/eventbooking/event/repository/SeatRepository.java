package com.eventbooking.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventbooking.event.entity.Seat;
import com.eventbooking.event.entity.SeatStatus;

import jakarta.persistence.LockModeType;

public interface SeatRepository
        extends JpaRepository<Seat, Long> {

    List<Seat> findByEventIdOrderBySeatRowAscSeatPositionAsc(
            Long eventId
    );

    List<Seat> findByEventIdAndStatusOrderBySeatRowAscSeatPositionAsc(
            Long eventId,
            SeatStatus status
    );

    long countByEventIdAndStatus(
            Long eventId,
            SeatStatus status
    );

    boolean existsByEventId(Long eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Seat s
            WHERE s.eventId = :eventId
              AND s.id IN :seatIds
            ORDER BY s.seatRow, s.seatPosition
            """)
    List<Seat> findSeatsForUpdate(
            @Param("eventId") Long eventId,
            @Param("seatIds") List<Long> seatIds
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Seat s
            WHERE s.eventId = :eventId
              AND s.holdReference = :holdReference
            """)
    List<Seat> findHeldSeatsForUpdate(
            @Param("eventId") Long eventId,
            @Param("holdReference") String holdReference
    );
}