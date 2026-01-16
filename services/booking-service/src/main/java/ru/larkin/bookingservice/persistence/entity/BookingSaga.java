package ru.larkin.bookingservice.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "booking_saga",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_booking_saga_booking_id", columnNames = {"booking_id"})
        },
        indexes = {
                @Index(name = "idx_booking_saga_request_id", columnList = "request_id")
        }
)
public class BookingSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @Column(name = "hold_token")
    private String holdToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public enum State {
        STARTED,
        HOLD_CONFIRMED,
        BOOKING_CONFIRMED,
        COMPENSATED
    }
}

