package ru.larkin.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.service.BookingService;

import java.util.UUID;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin / Bookings", description = "Администрирование бронирований")
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(
            summary = "Получить все бронирования",
            description = "Постраничный список всех бронирований (ADMIN).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Страница бронирований",
                    content = @Content(schema = @Schema(implementation = BookingDtoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content)
    })
    public Page<BookingDtoResponse> getAllBookings(
            @Parameter(description = "Параметры пагинации")
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return bookingService.getBookings(pageable);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить бронирование",
            description = "Удаляет бронирование по id (ADMIN).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Удалено", content = @Content),
            @ApiResponse(responseCode = "404", description = "Бронирование не найдено", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content)
    })
    public ResponseEntity<Void> deleteBookingById(
            @Parameter(description = "ID бронирования", required = true)
            @NotNull @PathVariable("id") UUID id
    ) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
