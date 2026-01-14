package ru.larkin.bookingservice.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.larkin.bookingservice.persistence.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
