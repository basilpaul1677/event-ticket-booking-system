package com.eventbooking.user.service;

import java.util.List;

import com.eventbooking.user.dto.EventCenterResponse;
import com.eventbooking.user.dto.RegisterEventCenterRequest;
import com.eventbooking.user.dto.RejectEventCenterRequest;

public interface EventCenterService 
{
    EventCenterResponse registerEventCenter(RegisterEventCenterRequest request);
    EventCenterResponse getEventCenterById(Long id);
    EventCenterResponse getEventCenterByUserId(Long userId);
    List<EventCenterResponse> getPendingEventCenters();
    EventCenterResponse approveEventCenter(Long id);
    EventCenterResponse rejectEventCenter(Long id,RejectEventCenterRequest request);
}