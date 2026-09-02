package com.eventbooking.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.user.entity.EventCenter;
import com.eventbooking.user.entity.EventCenterStatus;

public interface EventCenterRepository extends JpaRepository<EventCenter, Long> 
{
    Optional<EventCenter> findByUserId(Long userId);
    List<EventCenter> findByStatus(EventCenterStatus status);
    boolean existsByUserId(Long userId);
}