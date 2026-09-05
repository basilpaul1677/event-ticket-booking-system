package com.eventbooking.event.dto;

import java.time.LocalDateTime;
import java.util.List;

public record HoldSeatsResponse(
        Long eventId,
        String holdReference,
        LocalDateTime heldUntil,
        List<HeldSeatResponse> seats
)
{
}