package com.eventbooking.event.security;

public record JwtUserPrincipal(
        Long userId,
        String username,
        String role
) 
{
}