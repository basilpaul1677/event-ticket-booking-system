package com.eventbooking.event.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateEventRequest(
        @NotNull(message = "Event center ID is required")
        Long eventCenterId,

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must not exceed 100 characters")
        String category,

        @NotBlank(message = "Venue name is required")
        @Size(max = 150, message = "Venue name must not exceed 150 characters")
        String venueName,

        @NotBlank(message = "Venue address is required")
        @Size(max = 500, message = "Venue address must not exceed 500 characters")
        String venueAddress,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @NotNull(message = "Event date is required")
        @FutureOrPresent(message = "Event date must be today or in the future")
        LocalDate eventDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        @NotNull(message = "Total seats are required")
        @Min(value = 1, message = "Total seats must be at least 1")
        Integer totalSeats,

        @NotNull(message = "Ticket price is required")
        @DecimalMin(value = "0.0", inclusive = false,
                message = "Ticket price must be greater than 0")
        BigDecimal ticketPrice
)
{
}