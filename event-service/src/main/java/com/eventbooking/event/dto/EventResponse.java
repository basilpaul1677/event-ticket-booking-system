package com.eventbooking.event.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.eventbooking.event.entity.EventStatus;

public record EventResponse(
        Long id,
        Long eventCenterId,
        String title,
        String description,
        String category,
        String venueName,
        String venueAddress,
        String city,
        LocalDate eventDate,
        LocalTime startTime,
        LocalTime endTime,
        Integer totalSeats,
        BigDecimal ticketPrice,
        EventStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{
}