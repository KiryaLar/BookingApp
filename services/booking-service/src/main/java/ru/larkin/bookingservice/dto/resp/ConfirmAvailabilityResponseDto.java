package ru.larkin.bookingservice.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record ConfirmAvailabilityResponseDto(
        String token,
        @JsonFormat(shape = JsonFormat.Shape.STRING) OffsetDateTime expiresAt
) {
}

