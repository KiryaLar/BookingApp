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
import ru.larkin.hotelmanagementservice.dto.resp.RoomLoadStatResponse;
import ru.larkin.hotelmanagementservice.dto.resp.RoomResponse;
import ru.larkin.hotelmanagementservice.entity.Hotel;
import ru.larkin.hotelmanagementservice.entity.Room;
import ru.larkin.hotelmanagementservice.entity.RoomAvailabilityStatus;
import ru.larkin.hotelmanagementservice.entity.RoomHold;
import ru.larkin.hotelmanagementservice.repo.HotelRepository;
import ru.larkin.hotelmanagementservice.repo.RoomHoldRepository;
import ru.larkin.hotelmanagementservice.repo.RoomRepository;

import java.time.OffsetDateTime;
import java.util.Comparator;
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
                .availabilityStatus(RoomAvailabilityStatus.AVAILABLE)
                .roomNumber(request.roomNumber())
                .capacity(request.capacity())
                .pricePerNight(request.pricePerNight())
                .build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listAvailable(RoomsQuery query) {
        return roomRepository.findAvailableRooms(query.dateFrom(), query.dateTo()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listRecommended(RoomsQuery query) {
        return roomRepository.findRecommendedAvailableRooms(query.dateFrom(), query.dateTo()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ConfirmAvailabilityResponse confirmAvailability(UUID roomId, ConfirmAvailabilityRequest request) {
        // идемпотентность по requestId
        RoomHold existing = roomHoldRepository.findByRequestId(request.requestId()).orElse(null);
        if (existing != null) {
            // если requestId уже был обработан, просто возвращаем тот же результат
            return new ConfirmAvailabilityResponse(existing.getToken(), existing.getExpiresAt());
        }

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

        roomHoldRepository.save(RoomHold.builder()
                .room(room)
                .dateFrom(request.dateFrom())
                .dateTo(request.dateTo())
                .requestId(request.requestId())
                .token(token)
                .expiresAt(expiresAt)
                .build());
        return new ConfirmAvailabilityResponse(token, expiresAt);
    }

    @Transactional
    public void releaseHold(UUID roomId, ReleaseHoldRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        RoomHold hold = roomHoldRepository.findByToken(request.token())
                // идемпотентность: повторный release того же токена/запроса считаем успехом
                .orElse(null);

        if (hold == null) {
            return;
        }

        if (!hold.getRoom().getId().equals(room.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hold token does not belong to this room");
        }

        roomHoldRepository.delete(hold);
    }

    public List<RoomLoadStatResponse> getRoomsLoadStat(String order, Integer top) {
        Comparator<Room> comparator = Comparator.comparingLong(Room::getTimesBooked);
        if (!"asc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        long limit = (top == null) ? Long.MAX_VALUE : Math.max(0, top);

        return roomRepository.findAll().stream()
                .sorted(comparator)
                .limit(limit)
                .map(r ->
                        new RoomLoadStatResponse(
                                r.getId(),
                                r.getHotel().getId(),
                                r.getRoomNumber(),
                                r.getTimesBooked()
                        ))
                .toList();
    }

    protected RoomResponse toResponse(Room r) {
        return new RoomResponse(
                r.getId(),
                r.getHotel().getId(),
                r.getRoomNumber(),
                r.getCapacity(),
                r.getPricePerNight(),
                r.getTimesBooked()
        );
    }
}
