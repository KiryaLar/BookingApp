package ru.larkin.bookingservice.dto.req;

import jakarta.validation.constraints.NotBlank;

public record ReleaseHoldRequestDto(
        @NotBlank String token,
        @NotBlank String requestId
) {
}

