package com.eventbooking.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.booking.entity.Booking;
import com.eventbooking.booking.entity.BookingStatus;

public interface BookingRepository
        extends JpaRepository<Booking, Long> 
{
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findByEventIdOrderByCreatedAtDesc(Long eventId);
    List<Booking> findByStatus(BookingStatus status);
    boolean existsByBookingReference(String bookingReference);
}