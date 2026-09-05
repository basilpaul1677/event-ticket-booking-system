package com.eventbooking.event.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.event.dto.HeldSeatResponse;
import com.eventbooking.event.dto.HoldSeatsRequest;
import com.eventbooking.event.dto.HoldSeatsResponse;
import com.eventbooking.event.dto.SeatResponse;
import com.eventbooking.event.entity.Event;
import com.eventbooking.event.entity.Seat;
import com.eventbooking.event.entity.SeatStatus;
import com.eventbooking.event.exception.ResourceNotFoundException;
import com.eventbooking.event.repository.EventRepository;
import com.eventbooking.event.repository.SeatRepository;
import com.eventbooking.event.service.SeatService;

@Service
@Transactional
public class SeatServiceImpl implements SeatService {

    private static final long HOLD_DURATION_MINUTES = 10;

    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public SeatServiceImpl(
            SeatRepository seatRepository,
            EventRepository eventRepository) {

        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsByEvent(Long eventId) {

        validateEventExists(eventId);

        return seatRepository
                .findByEventIdOrderBySeatRowAscSeatPositionAsc(eventId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getAvailableSeats(Long eventId) {

        validateEventExists(eventId);

        return seatRepository
                .findByEventIdAndStatusOrderBySeatRowAscSeatPositionAsc(
                        eventId,
                        SeatStatus.AVAILABLE
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getAvailableSeatCount(Long eventId) {

        validateEventExists(eventId);

        return seatRepository.countByEventIdAndStatus(
                eventId,
                SeatStatus.AVAILABLE
        );
    }

    @Override
    public HoldSeatsResponse holdSeats(
            Long eventId,
            HoldSeatsRequest request,
            Long userId) {

        validateEventExists(eventId);

        if (request.seatIds() == null
                || request.seatIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one seat must be selected"
            );
        }

        Set<Long> uniqueSeatIds =
                new HashSet<>(request.seatIds());

        if (uniqueSeatIds.size() != request.seatIds().size()) {

            throw new IllegalArgumentException(
                    "Duplicate seat IDs are not allowed"
            );
        }

        List<Seat> seats =
                seatRepository.findSeatsForUpdate(
                        eventId,
                        request.seatIds()
                );

        if (seats.size() != uniqueSeatIds.size()) {

            throw new IllegalArgumentException(
                    "One or more selected seats do not belong to this event"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        releaseExpiredHolds(seats, now);

        for (Seat seat : seats) {

            if (seat.getStatus() != SeatStatus.AVAILABLE) {

                throw new IllegalStateException(
                        "Seat " + seat.getSeatNumber()
                                + " is not available"
                );
            }
        }

        LocalDateTime heldUntil =
                now.plusMinutes(HOLD_DURATION_MINUTES);

        for (Seat seat : seats) {

            seat.setStatus(SeatStatus.HELD);
            seat.setHoldReference(request.holdReference());
            seat.setHeldUntil(heldUntil);
        }

        seatRepository.saveAll(seats);

        List<HeldSeatResponse> heldSeats =
                seats.stream()
                        .map(this::mapToHeldSeatResponse)
                        .toList();

        return new HoldSeatsResponse(
                eventId,
                request.holdReference(),
                heldUntil,
                heldSeats
        );
    }

    @Override
    public void releaseHeldSeats(
            Long eventId,
            String holdReference) {

        validateEventExists(eventId);

        if (holdReference == null
                || holdReference.isBlank()) {

            throw new IllegalArgumentException(
                    "Hold reference is required"
            );
        }

        List<Seat> seats =
                seatRepository.findHeldSeatsForUpdate(
                        eventId,
                        holdReference
                );

        for (Seat seat : seats) {

            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHoldReference(null);
            seat.setHeldUntil(null);
        }

        seatRepository.saveAll(seats);
    }

    private void releaseExpiredHolds(
            List<Seat> seats,
            LocalDateTime now) {

        for (Seat seat : seats) {

            if (seat.getStatus() == SeatStatus.HELD
                    && seat.getHeldUntil() != null
                    && !seat.getHeldUntil().isAfter(now)) {

                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setHoldReference(null);
                seat.setHeldUntil(null);
            }
        }
    }

    private void validateEventExists(Long eventId) {

        eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + eventId
                        )
                );
    }

    private SeatResponse mapToResponse(Seat seat) {

        return new SeatResponse(
                seat.getId(),
                seat.getEventId(),
                seat.getSeatNumber(),
                seat.getSeatRow(),
                seat.getSeatPosition(),
                seat.getStatus()
        );
    }

    private HeldSeatResponse mapToHeldSeatResponse(
            Seat seat) {

        Event event = eventRepository.findById(
                        seat.getEventId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: "
                                        + seat.getEventId()
                        )
                );

        BigDecimal ticketPrice =
                event.getTicketPrice();

        return new HeldSeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                ticketPrice
        );
    }
}