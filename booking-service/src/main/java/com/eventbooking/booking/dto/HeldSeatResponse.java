package com.eventbooking.booking.dto;

import java.math.BigDecimal;

public record HeldSeatResponse(
        Long seatId,
        String seatNumber,
        BigDecimal seatPrice
)
{
}