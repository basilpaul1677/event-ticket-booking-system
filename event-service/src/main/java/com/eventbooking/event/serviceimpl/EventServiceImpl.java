package com.eventbooking.event.serviceimpl;

import com.eventbooking.event.dto.CreateEventRequest;
import com.eventbooking.event.dto.EventResponse;
import com.eventbooking.event.entity.Event;
import com.eventbooking.event.entity.EventStatus;
import com.eventbooking.event.exception.ResourceNotFoundException;
import com.eventbooking.event.repository.EventRepository;
import com.eventbooking.event.service.EventService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService 
{
    private final EventRepository eventRepository;
    @Override
    public EventResponse createEvent(CreateEventRequest request) 
    {
        validateEventTime(request);
        Event event = Event.builder()
                .eventCenterId(request.eventCenterId())
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .venueName(request.venueName())
                .venueAddress(request.venueAddress())
                .city(request.city())
                .eventDate(request.eventDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .totalSeats(request.totalSeats())
                .ticketPrice(request.ticketPrice())
                .status(EventStatus.PENDING)
                .build();
        Event savedEvent = eventRepository.save(event);
        return mapToResponse(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) 
    {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + id
                        ));
        return mapToResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() 
    {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getPublishedEvents() 
    {
        return eventRepository.findByStatus(EventStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByEventCenter(Long eventCenterId) 
    {
        return eventRepository
                .findByEventCenterId(eventCenterId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> searchEvents(String keyword) 
    {
        return eventRepository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCity(String city) 
    {
        return eventRepository
                .findByCityIgnoreCase(city)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCategory(String category) 
    {
        return eventRepository
                .findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void validateEventTime(CreateEventRequest request) 
    {
        if (!request.endTime().isAfter(request.startTime())) 
        {
            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }
    }

    private EventResponse mapToResponse(Event event) 
    {
        return new EventResponse(
                event.getId(),
                event.getEventCenterId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getVenueName(),
                event.getVenueAddress(),
                event.getCity(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getTotalSeats(),
                event.getTicketPrice(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}