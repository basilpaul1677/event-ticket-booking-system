package com.eventbooking.event.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event.dto.CreateEventRequest;
import com.eventbooking.event.dto.EventResponse;
import com.eventbooking.event.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController 
{
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request) 
    {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long id) 
    {
        return ResponseEntity.ok(
                eventService.getEventById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() 
    {
        return ResponseEntity.ok(
                eventService.getAllEvents()
        );
    }

    @GetMapping("/published")
    public ResponseEntity<List<EventResponse>> getPublishedEvents() 
    {
        return ResponseEntity.ok(
                eventService.getPublishedEvents()
        );
    }

    @GetMapping("/event-center/{eventCenterId}")
    public ResponseEntity<List<EventResponse>> getEventsByEventCenter(
            @PathVariable Long eventCenterId) 
    {
        return ResponseEntity.ok(
                eventService.getEventsByEventCenter(eventCenterId)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchEvents(
            @RequestParam String keyword) 
    {
        return ResponseEntity.ok(
                eventService.searchEvents(keyword)
        );
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<EventResponse>> getEventsByCity(
            @PathVariable String city) 
    {
        return ResponseEntity.ok(
                eventService.getEventsByCity(city)
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<EventResponse>> getEventsByCategory(
            @PathVariable String category) 
    {
        return ResponseEntity.ok(
                eventService.getEventsByCategory(category)
        );
    }

    @GetMapping("/admin/events/pending")
    public ResponseEntity<List<EventResponse>> getPendingEvents() 
    {
    return ResponseEntity.ok(
            eventService.getPendingEvents());
    }

    @PutMapping("/admin/events/{id}/approve")
    public ResponseEntity<EventResponse> approveEvent(
        @PathVariable Long id) 
    {
        return ResponseEntity.ok(
            eventService.approveEvent(id));
    }

    @PutMapping("/admin/events/{id}/reject")
    public ResponseEntity<EventResponse> rejectEvent(
    @PathVariable Long id) 
    {
        return ResponseEntity.ok(
            eventService.rejectEvent(id));
    }
}