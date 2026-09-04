package com.eventbooking.event.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.eventbooking.event.dto.EventCenterResponse;

@Component
public class EventCenterClient 
{
    private final RestClient restClient;
    public EventCenterClient(
            RestClient.Builder builder,
            @Value("${user-service.url}") String userServiceUrl) 
    {
        this.restClient = builder
                .baseUrl(userServiceUrl)
                .build();
    }

    public EventCenterResponse getEventCenterByUserId(Long userId) 
    {
        return restClient
                .get()
                .uri(
                        "/api/v1/event-centers/user/{userId}",
                        userId
                )
                .retrieve()
                .body(EventCenterResponse.class);
    }
}