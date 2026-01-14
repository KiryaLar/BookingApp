package ru.larkin.bookingservice.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Заглушка для получения текущего пользователя.
 *
 * Сейчас в проекте нет Spring Security/JWT, поэтому берём X-User-Id из запроса.
 * Позже можно заменить на SecurityContext (JWT claims / principal).
 */
@Component
public class CurrentUserProvider {

    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    public void set(UUID userId) {
        CURRENT.set(userId);
    }

    public void clear() {
        CURRENT.remove();
    }

    public Optional<UUID> getOptional() {
        return Optional.ofNullable(CURRENT.get());
    }

    public UUID require() {
        return getOptional().orElseThrow(() -> new ru.larkin.bookingservice.service.exception.UnauthorizedException(
                "Требуется идентификатор пользователя"));
    }
}

