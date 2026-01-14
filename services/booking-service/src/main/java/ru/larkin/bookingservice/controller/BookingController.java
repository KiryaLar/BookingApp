package ru.larkin.bookingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.dto.resp.SuccessResponse;
import ru.larkin.bookingservice.service.BookingService;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<BookingDtoResponse> createBooking(@Valid @RequestBody CreateBookingRequest createBookingRequest) {
        BookingDtoResponse created = bookingService.create(createBookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<SuccessResponse> deleteBookingById(@NotNull @PathVariable("id") UUID id) {
        bookingService.delete(id);
        SuccessResponse resp = new SuccessResponse();
        resp.setStatus(200);
        resp.setMessage("deleted");
        resp.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<BookingDtoResponse> getBookingById(@NotNull @PathVariable("id") UUID id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<BookingDtoResponse>> getBookings(
            @Min(0) @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @Min(1) @Max(100) @RequestParam(value = "size", required = false, defaultValue = "20") Integer size
    ) {
        return ResponseEntity.ok(bookingService.getPage(page, size));
    }
}

