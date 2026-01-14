package ru.larkin.hotelmanagementservice.dto.resp;

import java.math.BigDecimal;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        UUID hotelId,
        Integer number,
        Integer capacity,
        BigDecimal pricePerNight,
        Long timesBooked
) {
}

