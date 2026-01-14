package ru.larkin.hotelmanagementservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.larkin.hotelmanagementservice.dto.req.ConfirmAvailabilityRequest;
import ru.larkin.hotelmanagementservice.dto.req.CreateRoomRequest;
import ru.larkin.hotelmanagementservice.dto.req.ReleaseHoldRequest;
import ru.larkin.hotelmanagementservice.dto.req.RoomsQuery;
import ru.larkin.hotelmanagementservice.dto.resp.ConfirmAvailabilityResponse;
import ru.larkin.hotelmanagementservice.dto.resp.RoomResponse;
import ru.larkin.hotelmanagementservice.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@Validated
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // ADMIN
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse create(@Valid @RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

    // USER
    @GetMapping
    public List<RoomResponse> listAvailable(@Valid RoomsQuery query) {
        return roomService.listAvailable(query);
    }

    // USER
    @GetMapping("/recommend")
    public List<RoomResponse> recommend(@Valid RoomsQuery query) {
        return roomService.listRecommended(query);
    }

    // INTERNAL
    @PostMapping("/{id}/confirm-availability")
    public ConfirmAvailabilityResponse confirmAvailability(
            @PathVariable("id") long roomId,
            @Valid @RequestBody ConfirmAvailabilityRequest request
    ) {
        return roomService.confirmAvailability(roomId, request);
    }

    // INTERNAL (не публикуется через Gateway)
    @PostMapping("/{id}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(
            @PathVariable("id") long roomId,
            @Valid @RequestBody ReleaseHoldRequest request
    ) {
        roomService.releaseHold(roomId, request);
    }
}

