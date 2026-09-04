package com.eventbooking.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.event.entity.Seat;
import com.eventbooking.event.entity.SeatStatus;

public interface SeatRepository extends JpaRepository<Seat, Long> 
{
    List<Seat> findByEventIdOrderBySeatRowAscSeatPositionAsc(Long eventId);
    List<Seat> findByEventIdAndStatusOrderBySeatRowAscSeatPositionAsc(Long eventId,SeatStatus status);
    long countByEventIdAndStatus(Long eventId,SeatStatus status);
    boolean existsByEventId(Long eventId);
}