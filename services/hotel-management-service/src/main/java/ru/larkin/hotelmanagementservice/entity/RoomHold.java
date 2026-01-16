package ru.larkin.hotelmanagementservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "room_holds",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_room_holds_request_id", columnNames = {"request_id"})
        },
        indexes = {
                @Index(name = "idx_room_holds_room_id", columnList = "room_id"),
                @Index(name = "idx_room_holds_token", columnList = "token")
        }
)
public class RoomHold {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate dateFrom;

    @Column(nullable = false)
    private LocalDate dateTo;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "request_id", nullable = false)
    private String requestId;

}
