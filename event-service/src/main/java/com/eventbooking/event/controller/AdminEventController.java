package com.eventbooking.event.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event.dto.EventResponse;
import com.eventbooking.event.service.EventService;

@RestController
@RequestMapping("/api/v1/admin/events")
public class AdminEventController 
{
    private final EventService eventService;
    public AdminEventController(EventService eventService) 
    {
        this.eventService = eventService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<EventResponse>> getPendingEvents() 
    {
        return ResponseEntity.ok(
                eventService.getPendingEvents());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<EventResponse> approveEvent(
            @PathVariable Long id) 
    {
        return ResponseEntity.ok(
                eventService.approveEvent(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<EventResponse> rejectEvent(
            @PathVariable Long id) 
    {
        return ResponseEntity.ok(
                eventService.rejectEvent(id));
    }
}