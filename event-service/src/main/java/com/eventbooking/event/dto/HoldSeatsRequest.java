package com.eventbooking.event.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record HoldSeatsRequest(
        @NotEmpty(message = "At least one seat must be selected")
        List<Long> seatIds,

        @NotBlank(message = "Hold reference is required")
        String holdReference
)
{
}