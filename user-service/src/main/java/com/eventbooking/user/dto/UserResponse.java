package com.eventbooking.user.dto;

import com.eventbooking.user.entity.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        Boolean active
) 

{
}