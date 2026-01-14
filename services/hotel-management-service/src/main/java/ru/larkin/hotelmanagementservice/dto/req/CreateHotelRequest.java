package ru.larkin.hotelmanagementservice.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateHotelRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 500) String address
) {
}

