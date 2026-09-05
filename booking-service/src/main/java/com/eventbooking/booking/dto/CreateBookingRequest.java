package com.eventbooking.booking.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull(message = "Event ID is required")
        Long eventId,
    
        @NotEmpty(message = "At least one seat must be selected")
        List<Long> seatIds
)
{
}