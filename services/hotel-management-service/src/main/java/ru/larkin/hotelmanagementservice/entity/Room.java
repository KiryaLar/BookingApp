package ru.larkin.hotelmanagementservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rooms",
uniqueConstraints = {
           @UniqueConstraint(columnNames = {"hotel_id", "room_number"})
       })
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer roomNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private RoomAvailabilityStatus availabilityStatus;

    @Column(precision = 12, scale = 2)
    private BigDecimal pricePerNight;

    @Column(nullable = false)
    private long timesBooked = 0;
}

