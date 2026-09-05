package com.eventbooking.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.eventbooking.booking.entity.BookingStatus;

public record BookingResponse(
        Long id,
        Long userId,
        Long eventId,
        String bookingReference,
        Integer seatCount,
        BigDecimal totalAmount,
        BookingStatus status,
        List<BookingSeatResponse> seats,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{
}