package ru.larkin.bookingservice.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.larkin.bookingservice.service.exception.UnauthorizedException;

public final class AuthUtils {

    private AuthUtils() {
    }

    public static UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedException("Не авторизован");
        }

        Object principal = auth.getPrincipal();
        String sub;

        if (principal instanceof Jwt jwt) {
            sub = jwt.getSubject();
        } else {
            // fallback (на всякий случай)
            sub = String.valueOf(principal);
        }

        try {
            return UUID.fromString(sub);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Некорректный токен: subject не является UUID");
        }
    }
}

