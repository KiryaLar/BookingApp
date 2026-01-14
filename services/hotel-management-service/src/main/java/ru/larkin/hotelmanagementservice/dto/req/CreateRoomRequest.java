package ru.larkin.hotelmanagementservice.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateRoomRequest(
        @NotNull Long hotelId,
        @NotBlank @Size(max = 50) String number,
        @NotNull @Min(1) Integer capacity,
        @NotNull @Min(0) BigDecimal pricePerNight
) {
}

