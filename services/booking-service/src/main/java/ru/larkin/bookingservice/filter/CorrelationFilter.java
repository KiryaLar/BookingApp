package ru.larkin.bookingservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class CorrelationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String cid = request.getHeader("X-Correlation-Id");
        if (cid == null || cid.isBlank()) {
            cid = UUID.randomUUID().toString();
        }
        response.setHeader("X-Correlation-Id", cid);
        request.setAttribute("correlationId", cid);
        log.debug("[{}] {} {}", cid, request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
