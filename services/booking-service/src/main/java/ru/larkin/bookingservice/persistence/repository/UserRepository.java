package ru.larkin.bookingservice.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import ru.larkin.bookingservice.persistence.entity.User;

public interface UserRepository {

    User save(User entity);

    Optional<User> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
