package com.eventbooking.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.booking.entity.Payment;
import com.eventbooking.booking.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> 
    {
    Optional<Payment> findByPaymentReference(String paymentReference);
    Optional<Payment> findByBookingId(Long bookingId);
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Payment> findByStatus(PaymentStatus status);
    boolean existsByBookingId(Long bookingId);
    boolean existsByPaymentReference(String paymentReference);
}