package com.eventbooking.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProcessPaymentRequest(
        @NotBlank(message = "Payment method is required")
        @Size(
                max = 30,
                message = "Payment method must not exceed 30 characters"
        )
        String paymentMethod
) 
{
}