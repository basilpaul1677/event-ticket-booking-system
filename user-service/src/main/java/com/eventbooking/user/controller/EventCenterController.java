package com.eventbooking.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.user.dto.EventCenterResponse;
import com.eventbooking.user.dto.RegisterEventCenterRequest;
import com.eventbooking.user.service.EventCenterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/event-centers")
@RequiredArgsConstructor
public class EventCenterController 
{
    private final EventCenterService eventCenterService;

    @PostMapping("/register")
    public ResponseEntity<EventCenterResponse> registerEventCenter(@Valid @RequestBody RegisterEventCenterRequest request) 
    {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventCenterService.registerEventCenter(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventCenterResponse> getEventCenterById(@PathVariable Long id) 
    {
        return ResponseEntity.ok(
                eventCenterService.getEventCenterById(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<EventCenterResponse> getEventCenterByUserId(@PathVariable Long userId) 
    {
        return ResponseEntity.ok(
                eventCenterService.getEventCenterByUserId(userId)
        );
    }
}