package com.eventbooking.booking.config;

import com.eventbooking.booking.dto.ConfirmSeatsRequest;
import com.eventbooking.booking.dto.HoldSeatsRequest;
import com.eventbooking.booking.dto.HoldSeatsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(
            RestClient.Builder builder,
            @Value("${event-service.url}")
            String eventServiceUrl) {

        this.restClient = builder
                .baseUrl(eventServiceUrl)
                .build();
    }

    public HoldSeatsResponse holdSeats(
            Long eventId,
            HoldSeatsRequest request,
            String authorizationHeader) {

        return restClient
                .post()
                .uri(
                        "/api/v1/events/{eventId}/seats/hold",
                        eventId
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                )
                .body(request)
                .retrieve()
                .body(HoldSeatsResponse.class);
    }

    public void releaseSeats(
            Long eventId,
            String holdReference,
            String authorizationHeader) {

        restClient
                .delete()
                .uri(
                        "/api/v1/events/{eventId}/seats/hold/{holdReference}",
                        eventId,
                        holdReference
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                )
                .retrieve()
                .toBodilessEntity();
    }

    public void confirmSeats(Long eventId,String holdReference,String authorizationHeader) 
    {
        ConfirmSeatsRequest request =
                new ConfirmSeatsRequest(
                        holdReference
                );

        restClient
                .post()
                .uri(
                        "/api/v1/events/{eventId}/seats/confirm",
                        eventId
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        authorizationHeader
                )
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}