package ru.larkin.bookingservice.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.service.BookingService;

import java.util.UUID;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    public Page<BookingDtoResponse> getAllBookings(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return bookingService.getBookings(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingById(@NotNull @PathVariable("id") UUID id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
