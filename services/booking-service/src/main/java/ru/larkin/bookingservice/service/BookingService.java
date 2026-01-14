package ru.larkin.bookingservice.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.larkin.bookingservice.domain.BookingStatus;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.persistence.entity.Booking;
import ru.larkin.bookingservice.persistence.entity.User;
import ru.larkin.bookingservice.persistence.repository.BookingRepository;
import ru.larkin.bookingservice.persistence.repository.UserRepository;
import ru.larkin.bookingservice.service.exception.NotFoundException;
import ru.larkin.bookingservice.service.mapper.BookingMapper;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDtoResponse create(CreateBookingRequest req, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Booking entity = Booking.builder()
                .user(user)
                .hotelId(UUID.fromString(req.getHotelId()))
                .roomNumber(req.getRoomId())
                .status(BookingStatus.PENDING)
                .startDate(req.getFrom())
                .endDate(req.getTo())
                .build();

        bookingRepository.save(entity);
        return BookingMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public BookingDtoResponse getById(UUID id, UUID userId) {
        Booking entity = bookingRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        return BookingMapper.toResponse(entity);
    }

    public Page<BookingDtoResponse> getBookingsByUserId(Pageable pageable, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return bookingRepository.findAllByUser(user, pageable)
                .map(BookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingDtoResponse> getBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(BookingMapper::toResponse);
    }

    @Transactional
    public void delete(UUID id) {
        Booking entity = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        bookingRepository.delete(entity);
    }
}
