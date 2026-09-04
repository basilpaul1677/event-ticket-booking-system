package com.eventbooking.event.service;

import java.util.List;

import com.eventbooking.event.dto.CreateEventRequest;
import com.eventbooking.event.dto.EventResponse;

public interface EventService 
{
    EventResponse createEvent(CreateEventRequest request);
    EventResponse getEventById(Long id);
    List<EventResponse> getAllEvents();
    List<EventResponse> getPublishedEvents();
    List<EventResponse> getEventsByEventCenter(Long eventCenterId);
    List<EventResponse> searchEvents(String keyword);
    List<EventResponse> getEventsByCity(String city);
    List<EventResponse> getEventsByCategory(String category);
    List<EventResponse> getPendingEvents();
    EventResponse approveEvent(Long id);
    EventResponse rejectEvent(Long id);
}