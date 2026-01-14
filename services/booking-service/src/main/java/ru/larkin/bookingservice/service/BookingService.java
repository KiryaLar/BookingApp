package ru.larkin.bookingservice.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.larkin.bookingservice.domain.BookingStatus;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.persistence.entity.Booking;
import ru.larkin.bookingservice.persistence.repository.BookingRepository;
import ru.larkin.bookingservice.service.exception.NotFoundException;
import ru.larkin.bookingservice.service.mapper.BookingMapper;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CurrentUserProvider currentUserProvider;

    public BookingService(BookingRepository bookingRepository, CurrentUserProvider currentUserProvider) {
        this.bookingRepository = bookingRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public BookingDtoResponse create(CreateBookingRequest req) {
        UUID userId = currentUserProvider.require();

        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Booking entity = Booking.builder()
                .id(id)
                .userId(userId)
                .hotelId(UUID.fromString(req.getHotelId()))
                .roomNumber(req.getRoomId())
                .status(BookingStatus.PENDING)
                .startDate(req.getFrom())
                .endDate(req.getTo())
                .createdAt(now)
                .build();

        bookingRepository.save(entity);
        return BookingMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public BookingDtoResponse getById(UUID id) {
        UUID userId = currentUserProvider.require();
        Booking entity = bookingRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        return BookingMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<BookingDtoResponse> getPage(int page, int size) {
        UUID userId = currentUserProvider.require();
        return bookingRepository.findAllByUserIdOrderByCreatedAtDesc(userId, page, size)
                .stream()
                .map(BookingMapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        UUID userId = currentUserProvider.require();
        Booking entity = bookingRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        bookingRepository.delete(entity);
    }
}
