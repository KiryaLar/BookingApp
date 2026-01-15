package ru.larkin.hotelmanagementservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.larkin.hotelmanagementservice.dto.resp.RoomLoadStatResponse;
import ru.larkin.hotelmanagementservice.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
@Tag(name = "Stats", description = "Статистика по отелям/номерам")
public class StatController {

    private final RoomService roomService;

    /**
     * Статистика загруженности номеров.
     * По умолчанию: самые популярные (timesBooked DESC).
     *
     * @param order "desc" (по умолчанию) или "asc"
     * @param top   ограничение на количество записей (опционально)
     */
    @GetMapping("/rooms/load")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Статистика загруженности номеров",
            description = "Возвращает статистику по бронированиям (поле timesBooked).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика",
                    content = @Content(schema = @Schema(implementation = RoomLoadStatResponse.class))),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<RoomLoadStatResponse>> roomLoadStats(
            @Parameter(description = "Порядок сортировки: asc | desc", example = "desc")
            @RequestParam(name = "order", required = false, defaultValue = "desc") String order,
            @Parameter(description = "Ограничение по количеству записей", example = "10")
            @RequestParam(name = "top", required = false) Integer top
    ) {
        return ResponseEntity.ok(roomService.getRoomsLoadStat(order, top));
    }
}
