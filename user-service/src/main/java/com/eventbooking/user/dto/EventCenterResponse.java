package com.eventbooking.user.dto;

import com.eventbooking.user.entity.EventCenterStatus;

public record EventCenterResponse(
        Long id,
        Long userId,
        String ownerName,
        String email,
        String centerName,
        String address,
        String city,
        String state,
        String country,
        String contactNumber,
        String description,
        EventCenterStatus status,
        String rejectionReason
)

{
}