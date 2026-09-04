package com.eventbooking.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectEventCenterRequest(
        @NotBlank(message = "Rejection reason is required")
        @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
        String reason
) 
{
}