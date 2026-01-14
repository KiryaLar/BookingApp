package ru.larkin.hotelmanagementservice.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateRoomRequest(
        @NotNull UUID hotelId,
        @NotBlank @Min(1) Integer roomNumber,
        @NotNull @Min(1) Integer capacity,
        @NotNull @Min(0) BigDecimal pricePerNight
) {
}

