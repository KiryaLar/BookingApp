package ru.larkin.hotelmanagementservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.larkin.hotelmanagementservice.entity.RoomHold;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomHoldRepository extends JpaRepository<RoomHold, UUID> {
    Optional<RoomHold> findByToken(String token);

    List<RoomHold> findByExpiresAtBefore(OffsetDateTime timestamp);
}

