package ru.larkin.hotelmanagementservice.dto.resp;

import java.time.OffsetDateTime;

public record ConfirmAvailabilityResponse(
        String token,
        OffsetDateTime expiresAt
) {
}

