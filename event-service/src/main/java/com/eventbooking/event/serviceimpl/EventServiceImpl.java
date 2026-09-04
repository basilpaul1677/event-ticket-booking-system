package com.eventbooking.event.serviceimpl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.event.client.EventCenterClient;
import com.eventbooking.event.dto.CreateEventRequest;
import com.eventbooking.event.dto.EventCenterResponse;
import com.eventbooking.event.dto.EventResponse;
import com.eventbooking.event.entity.Event;
import com.eventbooking.event.entity.EventStatus;
import com.eventbooking.event.entity.Seat;
import com.eventbooking.event.entity.SeatStatus;
import com.eventbooking.event.exception.ResourceNotFoundException;
import com.eventbooking.event.repository.EventRepository;
import com.eventbooking.event.repository.SeatRepository;
import com.eventbooking.event.security.JwtUserPrincipal;
import com.eventbooking.event.service.EventService;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventCenterClient eventCenterClient;
    private final SeatRepository seatRepository;

    public EventServiceImpl(
            EventRepository eventRepository,
            EventCenterClient eventCenterClient,
            SeatRepository seatRepository) {

        this.eventRepository = eventRepository;
        this.eventCenterClient = eventCenterClient;
        this.seatRepository = seatRepository;
    }

    // ============================================================
    // CREATE EVENT
    // ============================================================

    @Override
    public EventResponse createEvent(CreateEventRequest request) {

        validateEventTime(
                request.startTime(),
                request.endTime()
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof JwtUserPrincipal)) {

            throw new IllegalStateException(
                    "Authenticated Event Center user is required"
            );
        }

        JwtUserPrincipal principal =
                (JwtUserPrincipal) authentication.getPrincipal();

        Long authenticatedUserId = principal.userId();

        EventCenterResponse eventCenter =
                eventCenterClient.getEventCenterByUserId(
                        authenticatedUserId
                );

        if (eventCenter == null) {

            throw new IllegalStateException(
                    "Event Center not found for authenticated user"
            );
        }

        /*
         * The authenticated Event Center can create events
         * only for its own Event Center.
         */
        if (!eventCenter.id().equals(request.eventCenterId())) {

            throw new IllegalStateException(
                    "You can create events only for your own Event Center"
            );
        }

        /*
         * Only APPROVED Event Centers can create events.
         */
        if (!"APPROVED".equalsIgnoreCase(
                eventCenter.status())) {

            throw new IllegalStateException(
                    "Event Center must be APPROVED before creating events"
            );
        }

        /*
         * Newly created events always require Admin approval.
         */
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

        Event savedEvent =
                eventRepository.save(event);

        return mapToResponse(savedEvent);
    }

    // ============================================================
    // GET EVENT BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + id
                        )
                );

        return mapToResponse(event);
    }

    // ============================================================
    // GET ALL EVENTS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {

        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET PUBLISHED EVENTS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getPublishedEvents() {

        return eventRepository
                .findByStatus(EventStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET EVENTS BY EVENT CENTER
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByEventCenter(
            Long eventCenterId) {

        return eventRepository
                .findByEventCenterId(eventCenterId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // SEARCH EVENTS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> searchEvents(
            String keyword) {

        if (keyword == null ||
                keyword.isBlank()) {

            return getPublishedEvents();
        }

        return eventRepository
                .findByTitleContainingIgnoreCase(
                        keyword.trim()
                )
                .stream()
                .filter(event ->
                        event.getStatus()
                                == EventStatus.PUBLISHED
                )
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET EVENTS BY CITY
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCity(
            String city) {

        return eventRepository
                .findByCityIgnoreCase(city)
                .stream()
                .filter(event ->
                        event.getStatus()
                                == EventStatus.PUBLISHED
                )
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET EVENTS BY CATEGORY
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCategory(
            String category) {

        return eventRepository
                .findByCategoryIgnoreCase(category)
                .stream()
                .filter(event ->
                        event.getStatus()
                                == EventStatus.PUBLISHED
                )
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET PENDING EVENTS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getPendingEvents() {

        return eventRepository
                .findByStatus(EventStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // APPROVE EVENT
    // ============================================================

    @Override
    public EventResponse approveEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + id
                        )
                );

        /*
         * Only PENDING events can be approved.
         */
        if (event.getStatus() != EventStatus.PENDING) {

            throw new IllegalStateException(
                    "Only PENDING events can be approved"
            );
        }

        /*
         * Admin approval publishes the event.
         */
        event.setStatus(EventStatus.PUBLISHED);

        Event updatedEvent =
                eventRepository.save(event);

        /*
         * Once the event is published, create its
         * seat inventory.
         */
        initializeSeats(updatedEvent);

        return mapToResponse(updatedEvent);
    }

    // ============================================================
    // REJECT EVENT
    // ============================================================

    @Override
    public EventResponse rejectEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + id
                        )
                );

        /*
         * Only PENDING events can be rejected.
         */
        if (event.getStatus() != EventStatus.PENDING) {

            throw new IllegalStateException(
                    "Only PENDING events can be rejected"
            );
        }

        event.setStatus(EventStatus.REJECTED);

        Event updatedEvent =
                eventRepository.save(event);

        return mapToResponse(updatedEvent);
    }

    // ============================================================
    // INITIALIZE SEATS
    // ============================================================

    private void initializeSeats(Event event) {

        /*
         * Prevent duplicate seat creation.
         *
         * An event must have only one seat inventory.
         */
        if (seatRepository.existsByEventId(event.getId())) {
            return;
        }

        int totalSeats = event.getTotalSeats();

        /*
         * Seats are arranged in rows of 10.
         *
         * Example:
         *
         * A1  A2  A3  A4  A5  A6  A7  A8  A9  A10
         * B1  B2  B3  B4  B5  B6  B7  B8  B9  B10
         * C1  C2  C3  C4  C5
         */
        int seatsPerRow = 10;

        List<Seat> seats =
                new ArrayList<>(totalSeats);

        for (int index = 1;
             index <= totalSeats;
             index++) {

            int rowNumber =
                    ((index - 1) / seatsPerRow) + 1;

            int position =
                    ((index - 1) % seatsPerRow) + 1;

            String rowLabel =
                    generateRowLabel(rowNumber);

            String seatNumber =
                    rowLabel + position;

            Seat seat = Seat.builder()
                    .eventId(event.getId())
                    .seatNumber(seatNumber)
                    .seatRow(rowLabel)
                    .seatPosition(position)
                    .status(SeatStatus.AVAILABLE)
                    .build();

            seats.add(seat);
        }

        seatRepository.saveAll(seats);
    }

    // ============================================================
    // GENERATE ROW LABEL
    // ============================================================

    private String generateRowLabel(int rowNumber) {

        StringBuilder label =
                new StringBuilder();

        int number = rowNumber;

        while (number > 0) {

            number--;

            label.insert(
                    0,
                    (char) ('A' + (number % 26))
            );

            number = number / 26;
        }

        return label.toString();
    }

    // ============================================================
    // VALIDATE EVENT TIME
    // ============================================================

    private void validateEventTime(
            LocalTime startTime,
            LocalTime endTime) {

        if (endTime.isBefore(startTime)
                || endTime.equals(startTime)) {

            throw new IllegalArgumentException(
                    "Event end time must be after start time"
            );
        }
    }

    // ============================================================
    // MAP ENTITY TO RESPONSE
    // ============================================================

    private EventResponse mapToResponse(
            Event event) {

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