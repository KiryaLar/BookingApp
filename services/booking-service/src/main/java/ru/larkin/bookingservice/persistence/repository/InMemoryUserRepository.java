package ru.larkin.bookingservice.persistence.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import ru.larkin.bookingservice.persistence.entity.User;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<UUID, User> store;

    public InMemoryUserRepository(Map<UUID, User> store) {
        this.store = store;
    }

    @Override
    public User save(User entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String target = email.trim().toLowerCase();
        return store.values().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().trim().toLowerCase().equals(target))
                .findFirst();
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return findByEmailIgnoreCase(email).isPresent();
    }
}

