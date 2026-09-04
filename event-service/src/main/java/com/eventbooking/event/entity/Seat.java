package com.eventbooking.event.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_event_number",
                        columnNames = {"event_id", "seat_number"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_seat_event_id",
                        columnList = "event_id"
                ),
                @Index(
                        name = "idx_seat_event_status",
                        columnList = "event_id, status"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the event this seat belongs to.
     *
     * No @ManyToOne relationship is used because
     * we want the seat model to remain simple and
     * service-owned.
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    /**
     * Human-readable seat label.
     *
     * Examples:
     * A1, A2, A3
     * B1, B2, B3
     */
    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    /**
     * Row identifier.
     *
     * Examples:
     * A, B, C
     */
    @Column(name = "seat_row", nullable = false, length = 10)
    private String seatRow;

    /**
     * Numeric position inside the row.
     */
    @Column(name = "seat_position", nullable = false)
    private Integer seatPosition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Optimistic locking.
     *
     * This becomes important later when multiple
     * users attempt to reserve seats concurrently.
     */
    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    protected void onCreate() 
    {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) 
        {
            status = SeatStatus.AVAILABLE;
        }

        if (version == null) 
        {
            version = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() 
    {
        updatedAt = LocalDateTime.now();
    }
}