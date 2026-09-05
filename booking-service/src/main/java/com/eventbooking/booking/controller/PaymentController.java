package com.eventbooking.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.booking.dto.PaymentResponse;
import com.eventbooking.booking.dto.ProcessPaymentRequest;
import com.eventbooking.booking.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController 
{
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) 
    {
        this.paymentService = paymentService;
    }

    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long bookingId,
            @Valid @RequestBody ProcessPaymentRequest request) 
    {
        return ResponseEntity.ok(
                paymentService.processPayment(
                        bookingId,
                        request
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) 
    {
        return ResponseEntity.ok(
                paymentService.getPaymentById(id)
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(@PathVariable Long bookingId) 
    {
        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(
                        bookingId
                )
        );
    }
}