package com.eventbooking.booking.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.booking.config.EventServiceClient;
import com.eventbooking.booking.entity.Booking;
import com.eventbooking.booking.entity.BookingSeatStatus;
import com.eventbooking.booking.entity.BookingStatus;
import com.eventbooking.booking.repository.BookingRepository;

@Service
public class BookingExpirationService {

    private final BookingRepository bookingRepository;
    private final EventServiceClient eventServiceClient;

    private final String internalAuthorizationHeader;

    public BookingExpirationService(
            BookingRepository bookingRepository,
            EventServiceClient eventServiceClient,
            @Value("${booking-service.internal-authorization:}")
            String internalAuthorizationHeader) {

        this.bookingRepository =
                bookingRepository;

        this.eventServiceClient =
                eventServiceClient;

        this.internalAuthorizationHeader =
                internalAuthorizationHeader;
    }

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void expirePendingBookings() {

        LocalDateTime now =
                LocalDateTime.now();

        List<Booking> expiredBookings =
                bookingRepository.findByStatus(
                        BookingStatus.PENDING
                )
                .stream()
                .filter(booking ->
                        booking.getExpiresAt() != null
                                && !booking.getExpiresAt()
                                .isAfter(now)
                )
                .toList();
        for (Booking booking :expiredBookings) 
        {
            try 
            {
                eventServiceClient.releaseSeats(
                        booking.getEventId(),
                        booking.getBookingReference(),
                        resolveAuthorizationHeader()
                );
                booking.setStatus(BookingStatus.EXPIRED);
                booking.setExpiresAt(null);
                booking.getBookingSeats()
                        .forEach(bookingSeat ->
                                bookingSeat.setStatus(
                                        BookingSeatStatus.RELEASED
                                )
                        );
                bookingRepository.save(booking);
            } 
            catch (Exception ex) 
            {
                /*
                 * Do not mark the booking as expired if
                 * Event Service could not release the seats.
                 *
                 * The next scheduler execution will retry.
                 */
            }
        }
    }

    private String resolveAuthorizationHeader() 
    {
        if (internalAuthorizationHeader != null&& !internalAuthorizationHeader.isBlank()) 
        {
            return internalAuthorizationHeader;
        }
        throw new IllegalStateException(
                "Internal authorization token is not configured"
        );
    }
}