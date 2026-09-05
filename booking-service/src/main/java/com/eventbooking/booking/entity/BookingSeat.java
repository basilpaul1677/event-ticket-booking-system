package com.eventbooking.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "booking_seats",
        indexes = {
                @Index(
                        name = "idx_booking_seat_booking_id",
                        columnList = "booking_id"
                ),
                @Index(
                        name = "idx_booking_seat_event_id",
                        columnList = "event_id"
                ),
                @Index(
                        name = "idx_booking_seat_seat_id",
                        columnList = "seat_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Booking to which this seat belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "booking_id",
            nullable = false
    )
    private Booking booking;

    /**
     * Event Service event ID.
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    /**
     * Event Service seat ID.
     */
    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    /**
     * Human-readable seat number.
     *
     * Example:
     * A1
     * A2
     * B5
     */
    @Column(
            name = "seat_number",
            nullable = false,
            length = 20
    )
    private String seatNumber;

    /**
     * Price captured at booking time.
     *
     * We store the price here instead of depending on
     * the Event Service price later.
     */
    @Column(
            name = "seat_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal seatPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingSeatStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() 
    {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) 
        {
            status = BookingSeatStatus.HELD;
        }
    }

    @PreUpdate
    protected void onUpdate() 
    {
        updatedAt = LocalDateTime.now();
    }
}