package ru.larkin.bookingservice.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.larkin.bookingservice.persistence.entity.Booking;

public interface BookingRepository {

    Booking save(Booking entity);

    Optional<Booking> findByIdAndUserId(UUID id, UUID userId);

    List<Booking> findAllByUserIdOrderByCreatedAtDesc(UUID userId, int page, int size);

    void delete(Booking entity);
}
