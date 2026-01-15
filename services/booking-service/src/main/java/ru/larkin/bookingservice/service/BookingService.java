package ru.larkin.bookingservice.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.larkin.bookingservice.domain.BookingStatus;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.persistence.entity.Booking;
import ru.larkin.bookingservice.persistence.entity.User;
import ru.larkin.bookingservice.persistence.repository.BookingRepository;
import ru.larkin.bookingservice.persistence.repository.UserRepository;
import ru.larkin.bookingservice.service.exception.NotFoundException;
import ru.larkin.bookingservice.service.mapper.BookingMapper;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String BOOKING_NOT_FOUND = "Бронирование не найдено";
    private static final String USER_NOT_FOUND = "Пользователь не найден";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDtoResponse create(CreateBookingRequest req, UUID userId) {
        validateDates(req.getFrom(), req.getTo());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Booking entity = Booking.builder()
                .user(user)
                .hotelId(UUID.fromString(req.getHotelId()))
                .roomId(UUID.fromString(req.getRoomId()))
                .status(BookingStatus.PENDING)
                .startDate(req.getFrom())
                .endDate(req.getTo())
                .build();

        bookingRepository.save(entity);
        return BookingMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public BookingDtoResponse getByIdForCurrentOrAdmin(UUID bookingId, UUID currentUserId) {
        if (isAdmin()) {
            Booking entity = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND));
            return BookingMapper.toResponse(entity);
        }

        Booking entity = bookingRepository.findByIdAndUserId(bookingId, currentUserId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND));
        return BookingMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<BookingDtoResponse> getBookingsByUserId(Pageable pageable, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

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
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND));
        bookingRepository.delete(entity);
    }

    private static void validateDates(OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from/to обязательны");
        }
        if (!to.isAfter(from)) {
            throw new IllegalArgumentException("to должно быть позже from");
        }
    }

    private static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority a : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(a.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
