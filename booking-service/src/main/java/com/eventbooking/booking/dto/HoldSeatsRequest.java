package com.eventbooking.booking.dto;

import java.util.List;

public record HoldSeatsRequest(
        List<Long> seatIds,
        String holdReference
)
{
}