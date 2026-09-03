package com.eventbooking.user.dto;

public record LoginResponse(
        String token,
        String type,
        Long userId,
        String email,
        String role
)
{
}