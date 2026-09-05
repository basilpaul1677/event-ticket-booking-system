package com.eventbooking.booking.dto;

import com.eventbooking.booking.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        Long userId,
        Long eventId,
        String bookingReference,
        Integer seatCount,
        BigDecimal totalAmount,
        BookingStatus status,
        List<BookingSeatResponse> seats,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) 
{
}