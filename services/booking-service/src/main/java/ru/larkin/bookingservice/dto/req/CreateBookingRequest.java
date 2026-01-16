package ru.larkin.bookingservice.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    /**
     * Идемпотентный идентификатор запроса. Используется при повторах/тайм-аутах.
     */
    private String requestId;

    @NotNull
    private String hotelId;

    @NotNull
    private String roomId;

    private OffsetDateTime from;
    private OffsetDateTime to;
}
