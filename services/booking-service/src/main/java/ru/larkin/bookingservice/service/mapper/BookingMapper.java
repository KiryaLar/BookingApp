package ru.larkin.bookingservice.service.mapper;

import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.persistence.entity.Booking;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingDtoResponse toResponse(Booking entity) {
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(entity.getId());
        bookingDtoResponse.setUserId(entity.getUser().getId());
        bookingDtoResponse.setHotelId(entity.getHotelId());
        bookingDtoResponse.setRoomId(entity.getRoomId());
        bookingDtoResponse.setStatus(entity.getStatus().name());
        bookingDtoResponse.setFrom(entity.getStartDate());
        bookingDtoResponse.setTo(entity.getEndDate());
        return bookingDtoResponse;
    }
}
