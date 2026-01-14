package ru.larkin.hotelmanagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.larkin.hotelmanagementservice.dto.req.ConfirmAvailabilityRequest;
import ru.larkin.hotelmanagementservice.dto.req.CreateRoomRequest;
import ru.larkin.hotelmanagementservice.dto.req.ReleaseHoldRequest;
import ru.larkin.hotelmanagementservice.dto.req.RoomsQuery;
import ru.larkin.hotelmanagementservice.dto.resp.ConfirmAvailabilityResponse;
import ru.larkin.hotelmanagementservice.dto.resp.RoomResponse;
import ru.larkin.hotelmanagementservice.entity.Hotel;
import ru.larkin.hotelmanagementservice.entity.Room;
import ru.larkin.hotelmanagementservice.repo.HotelRepository;
import ru.larkin.hotelmanagementservice.repo.RoomHoldRepository;
import ru.larkin.hotelmanagementservice.repo.RoomRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private static final int DEFAULT_HOLD_MINUTES = 10;

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomHoldRepository roomHoldRepository;

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.hotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Room saved = roomRepository.save(Room.builder()
                .hotel(hotel)
                .number(request.number())
                .capacity(request.capacity())
                .pricePerNight(request.pricePerNight())
                .build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listAvailable(RoomsQuery query) {
        validateDates(query.dateFrom(), query.dateTo());
        return roomRepository.findAvailableRooms(query.dateFrom(), query.dateTo()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listRecommended(RoomsQuery query) {
        validateDates(query.dateFrom(), query.dateTo());
        return roomRepository.findRecommendedAvailableRooms(query.dateFrom(), query.dateTo()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ConfirmAvailabilityResponse confirmAvailability(long roomId, ConfirmAvailabilityRequest request) {
        validateDates(request.dateFrom(), request.dateTo());

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        boolean available = roomRepository.findAvailableRooms(request.dateFrom(), request.dateTo()).stream()
                .anyMatch(r -> r.getId().equals(room.getId()));

        if (!available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for requested dates");
        }

        int minutes = request.holdMinutes() == null ? DEFAULT_HOLD_MINUTES : request.holdMinutes();
        if (minutes <= 0 || minutes > 1440) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "holdMinutes must be between 1 and 1440");
        }

        String token = UUID.randomUUID().toString();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(minutes);

        roomHoldRepository.save(new RoomHold(room, request.dateFrom(), request.dateTo(), token, expiresAt));
        return new ConfirmAvailabilityResponse(token, expiresAt);
    }

    @Transactional
    public void releaseHold(long roomId, ReleaseHoldRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        RoomHold hold = roomHoldRepository.findByToken(request.token())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hold token not found"));

        if (!hold.getRoom().getId().equals(room.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hold token does not belong to this room");
        }

        roomHoldRepository.delete(hold);
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateFrom/dateTo are required");
        }
        if (!from.isBefore(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateFrom must be before dateTo");
        }
    }

    protected RoomResponse toResponse(Room r) {
        return new RoomResponse(
                r.getId(),
                r.getHotel().getId(),
                r.getNumber(),
                r.getCapacity(),
                r.getPricePerNight(),
                r.getTimesBooked()
        );
    }
}

