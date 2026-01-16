package ru.larkin.bookingservice.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import ru.larkin.bookingservice.client.HotelManagementClient;
import ru.larkin.bookingservice.domain.BookingStatus;
import ru.larkin.bookingservice.dto.req.ConfirmAvailabilityRequestDto;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.dto.req.ReleaseHoldRequestDto;
import ru.larkin.bookingservice.dto.resp.BookingDtoResponse;
import ru.larkin.bookingservice.dto.resp.ConfirmAvailabilityResponseDto;
import ru.larkin.bookingservice.persistence.entity.Booking;
import ru.larkin.bookingservice.persistence.entity.User;
import ru.larkin.bookingservice.persistence.repository.BookingRepository;
import ru.larkin.bookingservice.persistence.repository.UserRepository;
import ru.larkin.bookingservice.service.exception.ConflictException;
import ru.larkin.bookingservice.service.exception.NotFoundException;
import ru.larkin.bookingservice.service.exception.ServiceUnavailableException;
import ru.larkin.bookingservice.service.mapper.BookingMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String BOOKING_NOT_FOUND = "Бронирование не найдено";
    private static final String USER_NOT_FOUND = "Пользователь не найден";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final HotelManagementClient hotelManagementClient;

    /**
     * Распределённая «двухшаговая согласованность» как последовательность локальных транзакций (saga):
     * 1) booking-service: создаём Booking(PENDING) локально.
     * 2) hotel-management-service: confirm-availability (hold) с requestId (идемпотентно).
     * 3) Успех -> CONFIRMED.
     * 4) Ошибка/таймаут -> CANCELLED + release hold (best-effort).
     */
    public BookingDtoResponse create(CreateBookingRequest req, UUID userId) {
        validateDates(req.getFrom(), req.getTo());

        String requestId = (req.getRequestId() == null || req.getRequestId().isBlank())
                ? UUID.randomUUID().toString()
                : req.getRequestId().trim();

        // идемпотентность на входе
        Booking existing = bookingRepository.findByRequestId(requestId).orElse(null);
        if (existing != null) {
            log.info("[bookingId={}] idempotent create hit, requestId={}, status={}",
                    existing.getId(), requestId, existing.getStatus());
            return BookingMapper.toResponse(existing);
        }

        UUID roomId = UUID.fromString(req.getRoomId());
        UUID hotelId = UUID.fromString(req.getHotelId());

        Booking booking = createPendingBooking(userId, requestId, hotelId, roomId, req.getFrom(), req.getTo());
        log.info("[bookingId={}] saga START, requestId={}, status=PENDING", booking.getId(), requestId);

        ConfirmAvailabilityResponseDto holdResp = null;
        try {
            ConfirmAvailabilityRequestDto holdReq = new ConfirmAvailabilityRequestDto(
                    req.getFrom().toLocalDate(),
                    req.getTo().toLocalDate(),
                    requestId,
                    null
            );

            holdResp = hotelManagementClient.confirmAvailability(roomId, holdReq);
            saveHoldToken(booking.getId(), holdResp.token());
            log.info("[bookingId={}] hold CONFIRMED, token={}, expiresAt={}",
                    booking.getId(), holdResp.token(), holdResp.expiresAt());

            Booking confirmed = setStatus(booking.getId(), BookingStatus.CONFIRMED);
            log.info("[bookingId={}] saga DONE, status=CONFIRMED", booking.getId());
            return BookingMapper.toResponse(confirmed);

        } catch (RestClientResponseException e) {
            int code = e.getStatusCode().value();
            log.warn("[bookingId={}] hotel-management error status={} (requestId={})", booking.getId(), code, requestId);

            if (code == 409) {
                compensate(booking.getId(), requestId, roomId, holdResp);
                throw new ConflictException("Номер недоступен на выбранные даты");
            }

            compensate(booking.getId(), requestId, roomId, holdResp);
            throw new ServiceUnavailableException("Ошибка подтверждения доступности номера", e);

        } catch (ResourceAccessException e) {
            log.warn("[bookingId={}] hotel-management timeout/network (requestId={}): {}",
                    booking.getId(), requestId, e.getMessage());
            compensate(booking.getId(), requestId, roomId, holdResp);
            throw new ServiceUnavailableException("Hotel Management Service недоступен/таймаут", e);

        } catch (RuntimeException e) {
            compensate(booking.getId(), requestId, roomId, holdResp);
            throw e;
        }
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

    @Transactional
    protected Booking createPendingBooking(UUID userId, String requestId, UUID hotelId, UUID roomId,
                                          OffsetDateTime from, OffsetDateTime to) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Booking entity = Booking.builder()
                .user(user)
                .hotelId(hotelId)
                .roomId(roomId)
                .status(BookingStatus.PENDING)
                .startDate(from)
                .endDate(to)
                .requestId(requestId)
                .build();

        return bookingRepository.save(entity);
    }

    @Transactional
    protected void saveHoldToken(UUID bookingId, String holdToken) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND));
        // идемпотентность: токен выставляем только если ещё не выставлен
        if (b.getHoldToken() == null) {
            b.setHoldToken(holdToken);
            bookingRepository.save(b);
        }
    }

    @Transactional
    protected Booking setStatus(UUID bookingId, BookingStatus status) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND));

        if (b.getStatus() == status) {
            return b;
        }

        // допустимые переходы
        if (b.getStatus() == BookingStatus.PENDING && (status == BookingStatus.CONFIRMED || status == BookingStatus.CANCELLED)) {
            b.setStatus(status);
            return bookingRepository.save(b);
        }

        // остальные переходы не делаем (идемпотентно)
        return b;
    }

    protected void compensate(UUID bookingId, String requestId, UUID roomId, ConfirmAvailabilityResponseDto holdResp) {
        try {
            Booking cancelled = setStatus(bookingId, BookingStatus.CANCELLED);
            log.info("[bookingId={}] saga COMPENSATE, status=CANCELLED", bookingId);

            String token = cancelled.getHoldToken();
            if (token == null && holdResp != null) {
                token = holdResp.token();
            }

            if (token != null && !token.isBlank()) {
                try {
                    // requestId для release (отдельный) — чтобы компенсирующий вызов был идемпотентным
                    String releaseRequestId = requestId + ":release";
                    hotelManagementClient.releaseHold(roomId, new ReleaseHoldRequestDto(token, releaseRequestId));
                    log.info("[bookingId={}] compensation OK, hold released", bookingId);
                } catch (Exception ex) {
                    log.warn("[bookingId={}] compensation FAILED to release hold: {}", bookingId, ex.getMessage());
                }
            }
        } catch (Exception ex) {
            log.warn("[bookingId={}] compensation FAILED: {}", bookingId, ex.getMessage());
        }
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
