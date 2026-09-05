package com.eventbooking.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.eventbooking.booking.entity.PaymentStatus;

public record PaymentResponse(
        Long id,
        Long bookingId,
        Long userId,
        String paymentReference,
        BigDecimal amount,
        String paymentMethod,
        PaymentStatus status,
        String transactionId,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
)
{
}