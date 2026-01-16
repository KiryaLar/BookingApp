package ru.larkin.bookingservice.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.larkin.bookingservice.persistence.entity.BookingSaga;

import java.util.Optional;
import java.util.UUID;

public interface BookingSagaRepository extends JpaRepository<BookingSaga, UUID> {

    Optional<BookingSaga> findByRequestId(String requestId);

    Optional<BookingSaga> findByBookingId(UUID bookingId);
}

