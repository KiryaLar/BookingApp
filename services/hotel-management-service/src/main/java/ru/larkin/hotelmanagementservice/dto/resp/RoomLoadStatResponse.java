package ru.larkin.hotelmanagementservice.dto.resp;

import java.util.UUID;

/**
 * Статистика загруженности номера.
 */
public record RoomLoadStatResponse(
        UUID roomId,
        UUID hotelId,
        Integer roomNumber,
        long timesBooked
) {
}

