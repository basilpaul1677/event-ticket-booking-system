package com.eventbooking.booking.serviceimpl;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.booking.config.EventServiceClient;
import com.eventbooking.booking.dto.PaymentResponse;
import com.eventbooking.booking.dto.ProcessPaymentRequest;
import com.eventbooking.booking.entity.Booking;
import com.eventbooking.booking.entity.BookingSeat;
import com.eventbooking.booking.entity.BookingSeatStatus;
import com.eventbooking.booking.entity.BookingStatus;
import com.eventbooking.booking.entity.Payment;
import com.eventbooking.booking.entity.PaymentStatus;
import com.eventbooking.booking.exception.ResourceNotFoundException;
import com.eventbooking.booking.repository.BookingRepository;
import com.eventbooking.booking.repository.PaymentRepository;
import com.eventbooking.booking.security.JwtUserPrincipal;
import com.eventbooking.booking.service.PaymentService;

@Service
@Transactional
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final EventServiceClient eventServiceClient;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            EventServiceClient eventServiceClient) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.eventServiceClient = eventServiceClient;
    }

    @Override
    public PaymentResponse processPayment(
            Long bookingId,
            ProcessPaymentRequest request) {

        Long authenticatedUserId =
                getAuthenticatedUserId();

        String authorizationHeader =
                getAuthorizationHeader();

        Booking booking =
                bookingRepository.findById(
                        bookingId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: "
                                        + bookingId
                        )
                );

        if (!booking.getUserId()
                .equals(authenticatedUserId)) {

            throw new IllegalStateException(
                    "You are not authorized to pay for this booking"
            );
        }

        if (booking.getStatus()
                != BookingStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending bookings can be paid"
            );
        }

        if (booking.getExpiresAt() != null
                && !booking.getExpiresAt()
                .isAfter(java.time.LocalDateTime.now())) {

            booking.setStatus(
                    BookingStatus.EXPIRED
            );

            bookingRepository.save(booking);

            releaseSeats(
                    booking,
                    authorizationHeader
            );

            throw new IllegalStateException(
                    "Booking has expired"
            );
        }

        if (paymentRepository.existsByBookingId(
                bookingId)) {

            throw new IllegalStateException(
                    "Payment has already been initiated for this booking"
            );
        }

        Payment payment =
                Payment.builder()
                        .bookingId(booking.getId())
                        .userId(booking.getUserId())
                        .paymentReference(
                                generatePaymentReference()
                        )
                        .amount(
                                booking.getTotalAmount()
                        )
                        .paymentMethod(
                                request.paymentMethod()
                        )
                        .status(
                                PaymentStatus.INITIATED
                        )
                        .build();

        payment =
                paymentRepository.save(payment);

        try {

            /*
             * Development payment processing.
             *
             * A real payment provider such as Stripe,
             * Razorpay, PayPal, etc. can be integrated here
             * later without changing the booking workflow.
             */

            String transactionId =
                    generateTransactionId();

            payment.setTransactionId(
                    transactionId
            );

            payment.setStatus(
                    PaymentStatus.SUCCESS
            );

            paymentRepository.save(payment);

            eventServiceClient.confirmSeats(
                    booking.getEventId(),
                    booking.getBookingReference(),
                    authorizationHeader
            );

            booking.setStatus(
                    BookingStatus.CONFIRMED
            );

            booking.setExpiresAt(null);

            for (BookingSeat bookingSeat :
                    booking.getBookingSeats()) {

                bookingSeat.setStatus(
                        BookingSeatStatus.BOOKED
                );
            }

            bookingRepository.save(booking);

            return mapToResponse(payment);

        } catch (Exception ex) {

            payment.setStatus(
                    PaymentStatus.FAILED
            );

            payment.setFailureReason(
                    "Payment processing failed"
            );

            paymentRepository.save(payment);

            try {

                releaseSeats(
                        booking,
                        authorizationHeader
                );

            } catch (Exception releaseException) {

                ex.addSuppressed(
                        releaseException
                );
            }

            booking.setStatus(
                    BookingStatus.FAILED
            );

            bookingRepository.save(booking);

            throw new IllegalStateException(
                    "Payment processing failed: "
                            + ex.getMessage(),
                    ex
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            Long id) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id: "
                                                + id
                                )
                        );

        validatePaymentAccess(payment);

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(
            Long bookingId) {

        Payment payment =
                paymentRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found for booking id: "
                                                + bookingId
                                )
                        );

        validatePaymentAccess(payment);

        return mapToResponse(payment);
    }

    private void releaseSeats(
            Booking booking,
            String authorizationHeader) {

        eventServiceClient.releaseSeats(
                booking.getEventId(),
                booking.getBookingReference(),
                authorizationHeader
        );
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

    private void validatePaymentAccess(Payment payment) 
    {
        Long authenticatedUserId = getAuthenticatedUserId();
        if (!payment.getUserId().equals(authenticatedUserId)) 
        {
            throw new IllegalStateException(
                    "You are not authorized to access this payment"
            );
        }
    }

    private String generatePaymentReference() 
    {
        return "PAY-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }

    private String generateTransactionId() 
    {
        return "TXN-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 24)
                .toUpperCase();
    }

    private PaymentResponse mapToResponse(Payment payment) 
    {
        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getUserId(),
                payment.getPaymentReference(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}