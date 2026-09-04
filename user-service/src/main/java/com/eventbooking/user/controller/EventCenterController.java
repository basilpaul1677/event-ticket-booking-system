package com.eventbooking.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.user.dto.EventCenterResponse;
import com.eventbooking.user.dto.RegisterEventCenterRequest;
import com.eventbooking.user.dto.RejectEventCenterRequest;
import com.eventbooking.user.service.EventCenterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class EventCenterController {

    private final EventCenterService eventCenterService;

    public EventCenterController(
            EventCenterService eventCenterService) {

        this.eventCenterService = eventCenterService;
    }

    // ==========================================
    // EVENT CENTER REGISTRATION
    // ==========================================

    @PostMapping("/event-centers/register")
    public ResponseEntity<EventCenterResponse> registerEventCenter(
            @Valid @RequestBody RegisterEventCenterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        eventCenterService.registerEventCenter(request)
                );
    }

    // ==========================================
    // GET EVENT CENTER
    // ==========================================

    @GetMapping("/event-centers/{id}")
    public ResponseEntity<EventCenterResponse> getEventCenterById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                eventCenterService.getEventCenterById(id)
        );
    }

    // ==========================================
    // GET EVENT CENTER BY USER
    // ==========================================

    @GetMapping("/event-centers/user/{userId}")
    public ResponseEntity<EventCenterResponse> getEventCenterByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                eventCenterService.getEventCenterByUserId(userId)
        );
    }

    // ==========================================
    // ADMIN - GET PENDING EVENT CENTERS
    // ==========================================

    @GetMapping("/admin/event-centers/pending")
    public ResponseEntity<List<EventCenterResponse>>
    getPendingEventCenters() {

        return ResponseEntity.ok(
                eventCenterService.getPendingEventCenters()
        );
    }

    // ==========================================
    // ADMIN - APPROVE EVENT CENTER
    // ==========================================

    @PutMapping("/admin/event-centers/{id}/approve")
    public ResponseEntity<EventCenterResponse> approveEventCenter(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                eventCenterService.approveEventCenter(id)
        );
    }

    // ==========================================
    // ADMIN - REJECT EVENT CENTER
    // ==========================================

    @PutMapping("/admin/event-centers/{id}/reject")
    public ResponseEntity<EventCenterResponse> rejectEventCenter(
            @PathVariable Long id,
            @Valid @RequestBody RejectEventCenterRequest request) {

        return ResponseEntity.ok(
                eventCenterService.rejectEventCenter(id, request)
        );
    }
}