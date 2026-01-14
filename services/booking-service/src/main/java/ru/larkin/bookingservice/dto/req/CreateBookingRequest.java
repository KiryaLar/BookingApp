package ru.larkin.bookingservice.dto.req;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class CreateBookingRequest {

    @NotNull
    private String hotelId;

    @NotNull
    private Integer roomId;

    private OffsetDateTime from;

    private OffsetDateTime to;

    public CreateBookingRequest() {
    }

    public CreateBookingRequest(String hotelId, Integer roomId) {
        this.hotelId = hotelId;
        this.roomId = roomId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public OffsetDateTime getFrom() {
        return from;
    }

    public void setFrom(OffsetDateTime from) {
        this.from = from;
    }

    public OffsetDateTime getTo() {
        return to;
    }

    public void setTo(OffsetDateTime to) {
        this.to = to;
    }
}

