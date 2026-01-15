package ru.larkin.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.service.BookingService;
import ru.larkin.bookingservice.security.AuthUtils;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Операции по бронированиям пользователя")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Создать бронирование",
            description = "Создаёт новое бронирование для текущего пользователя.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Бронирование создано",
            content = @Content(schema = @Schema(implementation = BookingDtoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content)
    @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content)
    public ResponseEntity<BookingDtoResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest createBookingRequest
    ) {
        UUID userId = AuthUtils.currentUserId();
        BookingDtoResponse createdBooking = bookingService.create(createBookingRequest, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBooking.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdBooking);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(
            summary = "Получить бронирование по id",
            description = "Возвращает бронирование. USER получает только своё, ADMIN — любое.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Бронирование",
            content = @Content(schema = @Schema(implementation = BookingDtoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено", content = @Content)
    @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content)
    public ResponseEntity<BookingDtoResponse> getBookingById(
            @Parameter(description = "ID бронирования", required = true)
            @NotNull @PathVariable("id") UUID id
    ) {
        UUID userId = AuthUtils.currentUserId();
        return ResponseEntity.ok(bookingService.getByIdForCurrentOrAdmin(id, userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Получить мои бронирования",
            description = "Постраничный список бронирований текущего пользователя (USER).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Страница бронирований",
            content = @Content(schema = @Schema(implementation = BookingDtoResponse.class)))
    @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content)
    public ResponseEntity<Page<BookingDtoResponse>> getBookings(
            @Parameter(description = "Параметры пагинации (page, size, sort)") Pageable pageable
    ) {
        UUID userId = AuthUtils.currentUserId();
        return ResponseEntity.ok(bookingService.getBookingsByUserId(pageable, userId));
    }
}
