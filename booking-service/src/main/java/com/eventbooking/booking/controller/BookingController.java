package com.eventbooking.booking.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.booking.dto.BookingResponse;
import com.eventbooking.booking.dto.CreateBookingRequest;
import com.eventbooking.booking.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController 
{
    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService) 
    {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) 
    {
        return ResponseEntity.ok(
                bookingService.createBooking(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id) 
    {
        return ResponseEntity.ok(
                bookingService.getBookingById(id)
        );
    }

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<BookingResponse> getBookingByReference(
            @PathVariable String bookingReference) 
    {
        return ResponseEntity.ok(
                bookingService.getBookingByReference(
                        bookingReference
                )
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings() 
    {
        return ResponseEntity.ok(
                bookingService.getMyBookings()
        );
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByEvent(
            @PathVariable Long eventId) 
    {
        return ResponseEntity.ok(
                bookingService.getBookingsByEvent(eventId)
        );
    }
}