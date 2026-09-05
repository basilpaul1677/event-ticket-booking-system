package com.eventbooking.booking.service;

import java.util.List;

import com.eventbooking.booking.dto.BookingResponse;
import com.eventbooking.booking.dto.CreateBookingRequest;

public interface BookingService 
{
    BookingResponse createBooking(CreateBookingRequest request);
    BookingResponse getBookingById(Long id);
    BookingResponse getBookingByReference(String bookingReference);
    List<BookingResponse> getMyBookings();
    List<BookingResponse> getBookingsByEvent(Long eventId);
}