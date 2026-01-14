package ru.larkin.bookingservice.dto.resp;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoResponse {
    private UUID id;
    private UUID userId;
    private String hotelId;
    private Integer roomNumber;
    private String status;
    private OffsetDateTime from;
    private OffsetDateTime to;
}

