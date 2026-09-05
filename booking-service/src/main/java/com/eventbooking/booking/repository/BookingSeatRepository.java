package com.eventbooking.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.booking.entity.BookingSeat;
import com.eventbooking.booking.entity.BookingSeatStatus;

public interface BookingSeatRepository
        extends JpaRepository<BookingSeat, Long> 
{
    List<BookingSeat> findByBookingId(Long bookingId);
    List<BookingSeat> findByBookingIdAndStatus(Long bookingId,BookingSeatStatus status);
    List<BookingSeat> findByEventIdAndSeatId(Long eventId,Long seatId);
}