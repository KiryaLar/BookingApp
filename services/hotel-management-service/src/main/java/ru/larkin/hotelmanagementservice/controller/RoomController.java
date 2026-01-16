package ru.larkin.hotelmanagementservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.larkin.hotelmanagementservice.dto.req.ConfirmAvailabilityRequest;
import ru.larkin.hotelmanagementservice.dto.req.CreateRoomRequest;
import ru.larkin.hotelmanagementservice.dto.req.ReleaseHoldRequest;
import ru.larkin.hotelmanagementservice.dto.req.RoomsQuery;
import ru.larkin.hotelmanagementservice.dto.resp.ConfirmAvailabilityResponse;
import ru.larkin.hotelmanagementservice.dto.resp.RoomResponse;
import ru.larkin.hotelmanagementservice.service.RoomService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/rooms", "/rooms"})
@Validated
@Tag(name = "Rooms", description = "Управление номерами и проверка доступности")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Добавить номер",
            description = "Создаёт номер в указанном отеле. Доступно роли ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Номер создан",
                    content = @Content(schema = @Schema(implementation = RoomResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content),
            @ApiResponse(responseCode = "404", description = "Отель не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content)
    })
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse createdRoom = roomService.createRoom(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRoom.id())
                .toUri();
        return ResponseEntity.created(location).body(createdRoom);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Получить список свободных номеров",
            description = "Возвращает все свободные номера на заданный период (без спец. сортировки).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список свободных номеров",
                    content = @Content(schema = @Schema(implementation = RoomResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации параметров", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<RoomResponse>> listAvailable(
            @Parameter(description = "Диапазон дат (dateFrom/dateTo)")
            @Valid @ModelAttribute RoomsQuery query
    ) {
        return ResponseEntity.ok(roomService.listAvailable(query));
    }

    @GetMapping("/recommend")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Получить список рекомендованных свободных номеров",
            description = "Те же свободные номера, но отсортированные по возрастанию timesBooked.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список рекомендованных номеров",
                    content = @Content(schema = @Schema(implementation = RoomResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации параметров", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ResponseEntity<List<RoomResponse>> listRecommended(
            @Parameter(description = "Диапазон дат (dateFrom/dateTo)")
            @Valid @ModelAttribute RoomsQuery query
    ) {
        return ResponseEntity.ok(roomService.listRecommended(query));
    }

    // старый алиас (если где-то уже используется)
    @GetMapping("/recommended")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RoomResponse>> listRecommendedAlias(
            @Valid @ModelAttribute RoomsQuery query
    ) {
        return ResponseEntity.ok(roomService.listRecommended(query));
    }

    // INTERNAL
    @PostMapping("/{id}/confirm-availability")
    @Operation(
            summary = "Подтвердить доступность номера (hold)",
            description = "Временная блокировка доступности номера на заданный период (шаг согласованности). INTERNAL эндпоинт.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно создан hold",
                    content = @Content(schema = @Schema(implementation = ConfirmAvailabilityResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content),
            @ApiResponse(responseCode = "404", description = "Номер не найден", content = @Content),
            @ApiResponse(responseCode = "409", description = "Номер недоступен на период", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public ConfirmAvailabilityResponse confirmAvailability(
            @Parameter(description = "ID номера", required = true)
            @PathVariable("id") UUID roomId,
            @Valid @RequestBody ConfirmAvailabilityRequest request
    ) {
        return roomService.confirmAvailability(roomId, request);
    }

    // INTERNAL (не публикуется через Gateway)
    @PostMapping("/{id}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Снять hold (компенсация)",
            description = "Компенсирующее действие: удалить временную блокировку слота. INTERNAL эндпоинт (не публикуется через Gateway).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Hold удалён", content = @Content),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content),
            @ApiResponse(responseCode = "404", description = "Номер/токен не найден", content = @Content),
            @ApiResponse(responseCode = "409", description = "Токен не принадлежит номеру", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    public void release(
            @Parameter(description = "ID номера", required = true)
            @PathVariable("id") UUID roomId,
            @Valid @RequestBody ReleaseHoldRequest request
    ) {
        roomService.releaseHold(roomId, request);
    }
}
