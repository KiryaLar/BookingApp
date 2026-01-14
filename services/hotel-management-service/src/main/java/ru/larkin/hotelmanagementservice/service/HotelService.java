package ru.larkin.hotelmanagementservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.larkin.hotelmanagementservice.dto.req.CreateHotelRequest;
import ru.larkin.hotelmanagementservice.dto.resp.HotelResponse;
import ru.larkin.hotelmanagementservice.entity.Hotel;
import ru.larkin.hotelmanagementservice.repo.HotelRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomService roomService;

    @Transactional
    public HotelResponse create(CreateHotelRequest request) {
        Hotel saved = hotelRepository.save(Hotel.builder()
                .name(request.name())
                .address(request.address())
                .build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> getHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private HotelResponse toResponse(Hotel h) {
        return new HotelResponse(
                h.getId(),
                h.getName(),
                h.getAddress(),
                h.getRooms().stream()
                        .map(roomService::toResponse)
                        .toList()
        );
    }
}

