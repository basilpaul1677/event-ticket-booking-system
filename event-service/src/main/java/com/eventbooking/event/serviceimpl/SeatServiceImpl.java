package com.eventbooking.event.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.event.dto.SeatResponse;
import com.eventbooking.event.entity.Seat;
import com.eventbooking.event.entity.SeatStatus;
import com.eventbooking.event.exception.ResourceNotFoundException;
import com.eventbooking.event.repository.EventRepository;
import com.eventbooking.event.repository.SeatRepository;
import com.eventbooking.event.service.SeatService;

@Service
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService 
{
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    public SeatServiceImpl(
            SeatRepository seatRepository,
            EventRepository eventRepository) 
    {
        this.seatRepository = seatRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<SeatResponse> getSeatsByEvent(Long eventId) 
    {
        validateEventExists(eventId);
        return seatRepository
                .findByEventIdOrderBySeatRowAscSeatPositionAsc(eventId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SeatResponse> getAvailableSeats(Long eventId) 
    {
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
    public long getAvailableSeatCount(Long eventId) 
    {
        validateEventExists(eventId);
        return seatRepository.countByEventIdAndStatus(eventId,SeatStatus.AVAILABLE);
    }

    private void validateEventExists(Long eventId) 
    {
        if (!eventRepository.existsById(eventId)) 
        {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
    }

    private SeatResponse mapToResponse(Seat seat) 
    {
        return new SeatResponse(
                seat.getId(),
                seat.getEventId(),
                seat.getSeatNumber(),
                seat.getSeatRow(),
                seat.getSeatPosition(),
                seat.getStatus()
        );
    }
}