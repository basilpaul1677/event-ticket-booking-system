package com.eventbooking.event.dto;

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
        String status,
        String rejectionReason
) 
{
}