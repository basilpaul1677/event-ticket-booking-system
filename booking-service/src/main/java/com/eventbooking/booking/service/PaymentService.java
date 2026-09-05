package com.eventbooking.booking.service;

import com.eventbooking.booking.dto.PaymentResponse;
import com.eventbooking.booking.dto.ProcessPaymentRequest;

public interface PaymentService 
{
    PaymentResponse processPayment(Long bookingId,ProcessPaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse getPaymentByBookingId(Long bookingId);
}