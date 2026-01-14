package ru.larkin.hotelmanagementservice.dto.req;

import jakarta.validation.constraints.NotBlank;

public record ReleaseHoldRequest(
        @NotBlank String token
) {
}

