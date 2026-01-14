package ru.larkin.hotelmanagementservice.dto.resp;

import java.util.List;
import java.util.UUID;

public record HotelResponse(
        UUID id,
        String name,
        String address,
        List<RoomResponse> rooms
) {
}

