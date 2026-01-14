package ru.larkin.bookingservice.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.larkin.bookingservice.service.CurrentUserProvider;

@Component
public class CurrentUserHeaderFilter extends OncePerRequestFilter {

    private final CurrentUserProvider currentUserProvider;

    public CurrentUserHeaderFilter(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String userId = request.getHeader("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                currentUserProvider.set(UUID.fromString(userId));
            }
            filterChain.doFilter(request, response);
        } finally {
            currentUserProvider.clear();
        }
    }
}

