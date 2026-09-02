package com.eventbooking.user.service;

import com.eventbooking.user.dto.EventCenterResponse;
import com.eventbooking.user.dto.RegisterEventCenterRequest;

public interface EventCenterService 
{
    EventCenterResponse registerEventCenter(RegisterEventCenterRequest request);
    EventCenterResponse getEventCenterById(Long id);
    EventCenterResponse getEventCenterByUserId(Long userId);
}