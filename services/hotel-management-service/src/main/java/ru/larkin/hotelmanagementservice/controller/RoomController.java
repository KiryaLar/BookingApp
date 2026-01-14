package ru.larkin.hotelmanagementservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.larkin.hotelmanagementservice.dto.req.ConfirmAvailabilityRequest;
import ru.larkin.hotelmanagementservice.dto.req.CreateRoomRequest;
import ru.larkin.hotelmanagementservice.dto.req.ReleaseHoldRequest;
import ru.larkin.hotelmanagementservice.dto.req.RoomsQuery;
import ru.larkin.hotelmanagementservice.dto.resp.ConfirmAvailabilityResponse;
import ru.larkin.hotelmanagementservice.dto.resp.HotelResponse;
import ru.larkin.hotelmanagementservice.dto.resp.RoomResponse;
import ru.larkin.hotelmanagementservice.service.RoomService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse createdRoom = roomService.createRoom(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRoom.id())
                .toUri();
        return ResponseEntity.created(location).body(createdRoom);
    }

    // USER
    @GetMapping
    public ResponseEntity<List<RoomResponse>> listAvailable(@Valid @ModelAttribute RoomsQuery query) {
        return ResponseEntity.ok(roomService.listAvailable(query));
    }

    // USER
    @GetMapping("/recommended")
    public ResponseEntity<List<RoomResponse>> listRecommended(@Valid @ModelAttribute RoomsQuery query) {
        return ResponseEntity.ok(roomService.listRecommended(query));
    }

    // INTERNAL
    @PostMapping("/{id}/confirm-availability")
    public ConfirmAvailabilityResponse confirmAvailability(
            @PathVariable("id") UUID roomId,
            @Valid @RequestBody ConfirmAvailabilityRequest request
    ) {
        return roomService.confirmAvailability(roomId, request);
    }

    // INTERNAL (не публикуется через Gateway)
    @PostMapping("/{id}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(
            @PathVariable("id") UUID roomId,
            @Valid @RequestBody ReleaseHoldRequest request
    ) {
        roomService.releaseHold(roomId, request);
    }
}

