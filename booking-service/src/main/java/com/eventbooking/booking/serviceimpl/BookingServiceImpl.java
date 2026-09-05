package com.eventbooking.booking.serviceimpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.booking.config.EventServiceClient;
import com.eventbooking.booking.dto.BookingResponse;
import com.eventbooking.booking.dto.BookingSeatResponse;
import com.eventbooking.booking.dto.CreateBookingRequest;
import com.eventbooking.booking.dto.HeldSeatResponse;
import com.eventbooking.booking.dto.HoldSeatsRequest;
import com.eventbooking.booking.dto.HoldSeatsResponse;
import com.eventbooking.booking.entity.Booking;
import com.eventbooking.booking.entity.BookingSeat;
import com.eventbooking.booking.entity.BookingSeatStatus;
import com.eventbooking.booking.entity.BookingStatus;
import com.eventbooking.booking.exception.ResourceNotFoundException;
import com.eventbooking.booking.repository.BookingRepository;
import com.eventbooking.booking.security.JwtUserPrincipal;
import com.eventbooking.booking.service.BookingService;

@Service
@Transactional
public class BookingServiceImpl
        implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventServiceClient eventServiceClient;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            EventServiceClient eventServiceClient) {

        this.bookingRepository = bookingRepository;
        this.eventServiceClient = eventServiceClient;
    }

    @Override
    public BookingResponse createBooking(
            CreateBookingRequest request) {

        Long userId =
                getAuthenticatedUserId();

        String authorizationHeader =
                getAuthorizationHeader();

        String bookingReference =
                generateBookingReference();

        HoldSeatsRequest holdRequest =
                new HoldSeatsRequest(
                        request.seatIds(),
                        bookingReference
                );

        HoldSeatsResponse holdResponse;

        try {

            holdResponse =
                    eventServiceClient.holdSeats(
                            request.eventId(),
                            holdRequest,
                            authorizationHeader
                    );

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Unable to hold selected seats: "
                            + ex.getMessage(),
                    ex
            );
        }

        try {

            if (holdResponse == null
                    || holdResponse.seats() == null
                    || holdResponse.seats().isEmpty()) {

                throw new IllegalStateException(
                        "No seats were held"
                );
            }

            BigDecimal totalAmount =
                    calculateTotalAmount(
                            holdResponse.seats()
                    );

            Booking booking =
                    Booking.builder()
                            .userId(userId)
                            .eventId(request.eventId())
                            .bookingReference(
                                    bookingReference
                            )
                            .seatCount(
                                    holdResponse.seats().size()
                            )
                            .totalAmount(
                                    totalAmount
                            )
                            .status(
                                    BookingStatus.PENDING
                            )
                            .expiresAt(
                                    holdResponse.heldUntil()
                            )
                            .build();

            for (HeldSeatResponse heldSeat :
                    holdResponse.seats()) {

                BookingSeat bookingSeat =
                        BookingSeat.builder()
                                .eventId(
                                        request.eventId()
                                )
                                .seatId(
                                        heldSeat.seatId()
                                )
                                .seatNumber(
                                        heldSeat.seatNumber()
                                )
                                .seatPrice(
                                        heldSeat.seatPrice()
                                )
                                .status(
                                        BookingSeatStatus.HELD
                                )
                                .build();

                booking.addBookingSeat(
                        bookingSeat
                );
            }

            Booking savedBooking =
                    bookingRepository.save(
                            booking
                    );

            return mapToResponse(
                    savedBooking
            );

        } catch (Exception ex) {

            try {

                eventServiceClient.releaseSeats(
                        request.eventId(),
                        bookingReference,
                        authorizationHeader
                );

            } catch (Exception releaseException) {

                ex.addSuppressed(
                        releaseException
                );
            }

            throw new IllegalStateException(
                    "Unable to create booking: "
                            + ex.getMessage(),
                    ex
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(
            Long id) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found with id: "
                                                + id
                                )
                        );

        validateBookingAccess(
                booking
        );

        return mapToResponse(
                booking
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingByReference(
            String bookingReference) {

        Booking booking =
                bookingRepository
                        .findByBookingReference(
                                bookingReference
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Booking not found with reference: "
                                                + bookingReference
                                )
                        );

        validateBookingAccess(
                booking
        );

        return mapToResponse(
                booking
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {

        Long userId =
                getAuthenticatedUserId();

        return bookingRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByEvent(
            Long eventId) {

        return bookingRepository
                .findByEventIdOrderByCreatedAtDesc(
                        eventId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private BigDecimal calculateTotalAmount(
            List<HeldSeatResponse> seats) {

        BigDecimal total =
                BigDecimal.ZERO;

        for (HeldSeatResponse seat :
                seats) {

            if (seat.seatPrice() == null) {

                throw new IllegalStateException(
                        "Seat price cannot be null"
                );
            }

            total =
                    total.add(
                            seat.seatPrice()
                    );
        }

        return total;
    }

    private String generateBookingReference() {

        return "BK-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }

    private Long getAuthenticatedUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof JwtUserPrincipal principal)) {

            throw new IllegalStateException(
                    "Authenticated user is required"
            );
        }

        return principal.userId();
    }

    private String getAuthorizationHeader() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {

            throw new IllegalStateException(
                    "Authenticated user is required"
            );
        }

        Object details =
                authentication.getDetails();

        if (details instanceof String authorizationHeader
                && authorizationHeader.startsWith(
                "Bearer ")) {

            return authorizationHeader;
        }

        throw new IllegalStateException(
                "Authorization token is unavailable"
        );
    }

    private void validateBookingAccess(
            Booking booking) {

        Long authenticatedUserId =
                getAuthenticatedUserId();

        if (!booking.getUserId()
                .equals(authenticatedUserId)) {

            throw new IllegalStateException(
                    "You are not authorized to access this booking"
            );
        }
    }

    private BookingResponse mapToResponse(
            Booking booking) {

        List<BookingSeatResponse> seats =
                booking.getBookingSeats()
                        .stream()
                        .map(
                                this::mapSeatToResponse
                        )
                        .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getEventId(),
                booking.getBookingReference(),
                booking.getSeatCount(),
                booking.getTotalAmount(),
                booking.getStatus(),
                seats,
                booking.getExpiresAt(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    private BookingSeatResponse mapSeatToResponse(
            BookingSeat bookingSeat) {

        return new BookingSeatResponse(
                bookingSeat.getId(),
                bookingSeat.getSeatId(),
                bookingSeat.getSeatNumber(),
                bookingSeat.getSeatPrice(),
                bookingSeat.getStatus()
        );
    }
}