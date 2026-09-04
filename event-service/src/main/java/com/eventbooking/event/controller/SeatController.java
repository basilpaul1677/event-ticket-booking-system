package com.eventbooking.event.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event.dto.SeatResponse;
import com.eventbooking.event.service.SeatService;

@RestController
@RequestMapping("/api/v1/events/{eventId}/seats")
public class SeatController 
{
    private final SeatService seatService;
    public SeatController(SeatService seatService) 
    {
        this.seatService = seatService;
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long eventId) 
    {
        return ResponseEntity.ok(
                seatService.getSeatsByEvent(eventId)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(@PathVariable Long eventId) 
    {
        return ResponseEntity.ok(
                seatService.getAvailableSeats(eventId)
        );
    }

    @GetMapping("/available/count")
    public ResponseEntity<Long> getAvailableSeatCount(@PathVariable Long eventId) 
    {
        return ResponseEntity.ok(
                seatService.getAvailableSeatCount(eventId)
        );
    }
}