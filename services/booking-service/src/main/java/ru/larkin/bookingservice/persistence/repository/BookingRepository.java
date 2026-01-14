package ru.larkin.bookingservice.persistence.repository;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.larkin.bookingservice.persistence.entity.Booking;
import ru.larkin.bookingservice.persistence.entity.User;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    Optional<Booking> findByIdAndUserId(UUID id, UUID userId);

    Page<Booking> findAllByUser(User user, Pageable pageable);
}
