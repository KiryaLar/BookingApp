package ru.larkin.bookingservice.persistence;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.larkin.bookingservice.persistence.entity.Booking;
import ru.larkin.bookingservice.persistence.entity.User;

@Configuration
public class InMemoryRepositoriesConfig {

    @Bean
    public Map<UUID, User> usersStore() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<UUID, Booking> bookingsStore() {
        return new ConcurrentHashMap<>();
    }
}

