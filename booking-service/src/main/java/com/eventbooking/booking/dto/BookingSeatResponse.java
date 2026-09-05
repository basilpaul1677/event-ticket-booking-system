package com.eventbooking.booking.dto;

import java.math.BigDecimal;

import com.eventbooking.booking.entity.BookingSeatStatus;

public record BookingSeatResponse(
        Long id,
        Long seatId,
        String seatNumber,
        BigDecimal seatPrice,
        BookingSeatStatus status
)
{
}