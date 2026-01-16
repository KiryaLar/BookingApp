package ru.larkin.bookingservice.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "clients.hotel-management")
public record HotelManagementClientProperties(
        String baseUrl,
        Duration timeout,
        int maxAttempts,
        Duration initialBackoff
) {
}

