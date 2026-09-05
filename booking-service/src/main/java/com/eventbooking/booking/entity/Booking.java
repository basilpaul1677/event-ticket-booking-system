package com.eventbooking.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(
                        name = "idx_booking_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_booking_event_id",
                        columnList = "event_id"
                ),
                @Index(
                        name = "idx_booking_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_booking_reference",
                        columnList = "booking_reference"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_booking_reference",
                        columnNames = "booking_reference"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Application user who owns the booking.
     *
     * This references the User Service identity only.
     * No cross-service JPA relationship is used.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Event being booked.
     *
     * Event data belongs to Event Service.
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    /**
     * Human-readable unique booking reference.
     */
    @Column(
            name = "booking_reference",
            nullable = false,
            unique = true,
            length = 50
    )
    private String bookingReference;

    /**
     * Number of seats included in this booking.
     */
    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    /**
     * Total booking amount.
     */
    @Column(
            name = "total_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Selected seats belonging to this booking.
     */
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @PrePersist
    protected void onCreate() 
    {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) 
        {
            status = BookingStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() 
    {
        updatedAt = LocalDateTime.now();
    }

    public void addBookingSeat(BookingSeat bookingSeat) 
    {
        bookingSeats.add(bookingSeat);
        bookingSeat.setBooking(this);
    }
}