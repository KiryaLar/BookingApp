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
@Table(name = "bookings")
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
    private Integer roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private OffsetDateTime startDate;

    private OffsetDateTime endDate;

    @Column(nullable = false)
    @CreatedDate
    private OffsetDateTime createdAt;

}

