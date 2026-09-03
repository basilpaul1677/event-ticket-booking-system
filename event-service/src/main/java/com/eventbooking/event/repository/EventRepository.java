package com.eventbooking.event.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.event.entity.Event;
import com.eventbooking.event.entity.EventStatus;

public interface EventRepository extends JpaRepository<Event, Long> 
{
    List<Event> findByStatus(EventStatus status);
    List<Event> findByEventCenterId(Long eventCenterId);
    List<Event> findByCityIgnoreCase(String city);
    List<Event> findByCategoryIgnoreCase(String category);
    List<Event> findByTitleContainingIgnoreCase(String title);
}