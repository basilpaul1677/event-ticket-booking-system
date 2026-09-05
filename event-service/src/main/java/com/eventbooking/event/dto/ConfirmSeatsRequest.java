package com.eventbooking.event.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmSeatsRequest(
        @NotBlank(message = "Hold reference is required")
        String holdReference
)
{
}