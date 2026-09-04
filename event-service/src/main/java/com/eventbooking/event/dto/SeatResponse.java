package com.eventbooking.event.dto;

import com.eventbooking.event.entity.SeatStatus;

public record SeatResponse(
        Long id,
        Long eventId,
        String seatNumber,
        String seatRow,
        Integer seatPosition,
        SeatStatus status
)
{
}