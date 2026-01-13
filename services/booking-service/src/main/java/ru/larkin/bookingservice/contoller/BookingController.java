package ru.larkin.bookingservice.contoller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.larkin.bookingservice.api.BookingsApi;
import ru.larkin.bookingservice.model.Booking;
import ru.larkin.bookingservice.model.CreateBookingRequest;
import ru.larkin.bookingservice.model.SuccessResponse;

@RestController
public class BookingController implements BookingsApi {

    @Override
    public ResponseEntity<Booking> createBooking(@Valid CreateBookingRequest createBookingRequest) {
        return BookingsApi.super.createBooking(createBookingRequest);
    }

    @Override
    public ResponseEntity<SuccessResponse> deleteBookingById(@NotNull UUID id) {
        return BookingsApi.super.deleteBookingById(id);
    }

    @Override
    public ResponseEntity<Booking> getBookingById(@NotNull UUID id) {
        return BookingsApi.super.getBookingById(id);
    }

    @Override
    public ResponseEntity<List<Booking>> getBookings(
            @Min(0) Integer page,
            @Min(1) @Max(100) Integer size
    ) {
        return BookingsApi.super.getBookings(page, size);
    }
}
