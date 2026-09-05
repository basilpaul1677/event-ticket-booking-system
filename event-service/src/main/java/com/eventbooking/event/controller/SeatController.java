package com.eventbooking.event.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event.dto.HoldSeatsRequest;
import com.eventbooking.event.dto.HoldSeatsResponse;
import com.eventbooking.event.dto.SeatResponse;
import com.eventbooking.event.security.JwtUserPrincipal;
import com.eventbooking.event.service.SeatService;

import jakarta.validation.Valid;

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
    public ResponseEntity<List<SeatResponse>> getSeats(
            @PathVariable Long eventId) 
    {
        return ResponseEntity.ok(
                seatService.getSeatsByEvent(eventId)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<List<SeatResponse>> getAvailableSeats(
            @PathVariable Long eventId) 
    {
        return ResponseEntity.ok(
                seatService.getAvailableSeats(eventId)
        );
    }

    @GetMapping("/available/count")
    public ResponseEntity<Long> getAvailableSeatCount(
            @PathVariable Long eventId) 
    {
        return ResponseEntity.ok(
                seatService.getAvailableSeatCount(eventId)
        );
    }

    @PostMapping("/hold")
    public ResponseEntity<HoldSeatsResponse> holdSeats(
            @PathVariable Long eventId,
            @Valid @RequestBody HoldSeatsRequest request) 
    {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(
                seatService.holdSeats(
                        eventId,
                        request,
                        userId
                )
        );
    }

    @DeleteMapping("/hold/{holdReference}")
    public ResponseEntity<Void> releaseHeldSeats(
            @PathVariable Long eventId,
            @PathVariable String holdReference) 
    {
        seatService.releaseHeldSeats(
                eventId,
                holdReference
        );
        return ResponseEntity.noContent().build();
    }

    private Long getAuthenticatedUserId() 
    {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null|| !(authentication.getPrincipal()instanceof JwtUserPrincipal principal)) 
                {
            throw new IllegalStateException(
                    "Authenticated user is required"
            );
        }
        return principal.userId();
    }
}