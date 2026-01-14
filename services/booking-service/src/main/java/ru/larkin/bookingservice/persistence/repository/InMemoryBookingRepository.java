package ru.larkin.bookingservice.persistence.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import ru.larkin.bookingservice.persistence.entity.Booking;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<UUID, Booking> store;

    public InMemoryBookingRepository(Map<UUID, Booking> store) {
        this.store = store;
    }

    @Override
    public Booking save(Booking entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Booking> findByIdAndUserId(UUID id, UUID userId) {
        Booking entity = store.get(id);
        if (entity == null) {
            return Optional.empty();
        }
        if (!entity.getUserId().equals(userId)) {
            return Optional.empty();
        }
        return Optional.of(entity);
    }

    @Override
    public List<Booking> findAllByUserIdOrderByCreatedAtDesc(UUID userId, int page, int size) {
        List<Booking> all = store.values().stream()
                .filter(b -> b.getUserId().equals(userId))
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .toList();

        int from = Math.max(page, 0) * Math.max(size, 1);
        if (from >= all.size()) {
            return List.of();
        }
        int to = Math.min(from + Math.max(size, 1), all.size());
        return new ArrayList<>(all.subList(from, to));
    }

    @Override
    public void delete(Booking entity) {
        store.remove(entity.getId());
    }
}

