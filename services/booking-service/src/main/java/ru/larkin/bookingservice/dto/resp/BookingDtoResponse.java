package ru.larkin.bookingservice.dto.resp;

import lombok.*;
import ru.larkin.bookingservice.domain.BookingStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoResponse {
    private UUID id;
    private String hotelId;
    private Integer roomId;
    private BookingStatus status;
    private OffsetDateTime from;
    private OffsetDateTime to;
}

