package ru.larkin.bookingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.service.BookingService;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDtoResponse> createBooking(@Valid @RequestBody CreateBookingRequest createBookingRequest) {
//        UUID userId = SecurityContextUtil.getCurrentUserId();
        //TODO: Достать id из security context
        BookingDtoResponse createdBooking = bookingService.create(createBookingRequest, UUID.randomUUID());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBooking.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdBooking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDtoResponse> getBookingById(@NotNull @PathVariable("id") UUID id) {
//        UUID userId = SecurityContextUtil.getCurrentUserId();
        //TODO: Достать id из security context
        return ResponseEntity.ok(bookingService.getById(id, UUID.randomUUID()));
    }

    @GetMapping
    public ResponseEntity<Page<BookingDtoResponse>> getBookings(Pageable pageable) {
//        UUID userId = SecurityContextUtil.getCurrentUserId();
        //TODO: Достать id из security context
        return ResponseEntity.ok(bookingService.getBookingsByUserId(pageable, UUID.randomUUID()));
    }
}
