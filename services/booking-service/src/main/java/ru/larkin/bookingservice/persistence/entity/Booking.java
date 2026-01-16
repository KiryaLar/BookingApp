package ru.larkin.bookingservice.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import ru.larkin.bookingservice.domain.BookingStatus;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "bookings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_bookings_request_id", columnNames = {"request_id"})
        },
        indexes = {
                @Index(name = "idx_bookings_user_id", columnList = "user_id"),
                @Index(name = "idx_bookings_room_id", columnList = "room_id")
        }
)
@EnableJpaAuditing
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private UUID hotelId;

    @Column(nullable = false)
    private UUID roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private OffsetDateTime startDate;

    private OffsetDateTime endDate;

    @Column(nullable = false)
    @CreatedDate
    private OffsetDateTime createdAt;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "hold_token")
    private String holdToken;

}
