package com.eventbooking.event.service;

import java.util.List;

import com.eventbooking.event.dto.ConfirmSeatsRequest;
import com.eventbooking.event.dto.HoldSeatsRequest;
import com.eventbooking.event.dto.HoldSeatsResponse;
import com.eventbooking.event.dto.SeatResponse;

public interface SeatService 
{
    List<SeatResponse> getSeatsByEvent(Long eventId);
    List<SeatResponse> getAvailableSeats(Long eventId);
    long getAvailableSeatCount(Long eventId);
    HoldSeatsResponse holdSeats(Long eventId,HoldSeatsRequest request,Long userId);
    void releaseHeldSeats(Long eventId,String holdReference);
    void confirmHeldSeats(Long eventId,ConfirmSeatsRequest request);
}