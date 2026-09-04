package com.eventbooking.event.service;

import java.util.List;

import com.eventbooking.event.dto.SeatResponse;

public interface SeatService 
{
    List<SeatResponse> getSeatsByEvent(Long eventId);
    List<SeatResponse> getAvailableSeats(Long eventId);
    long getAvailableSeatCount(Long eventId);
}