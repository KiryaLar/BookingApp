package ru.larkin.bookingservice.service.mapper;

import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.persistence.entity.Booking;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingDtoResponse toResponse(Booking entity) {
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(entity.getId());
        bookingDtoResponse.setHotelId(entity.getHotelId().toString());
        bookingDtoResponse.setRoomId(entity.getRoomNumber());
        bookingDtoResponse.setStatus(BookingDtoResponse.StatusEnum.valueOf(entity.getStatus().name()));
        bookingDtoResponse.setFrom(entity.getStartDate());
        bookingDtoResponse.setTo(entity.getEndDate());
        bookingDtoResponse.setCreatedAt(entity.getCreatedAt());
        return bookingDtoResponse;
    }
}
