package ru.larkin.bookingservice.client;

import java.time.Duration;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import ru.larkin.bookingservice.dto.req.ConfirmAvailabilityRequestDto;
import ru.larkin.bookingservice.dto.req.ReleaseHoldRequestDto;
import ru.larkin.bookingservice.dto.resp.ConfirmAvailabilityResponseDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotelManagementClient {

    private final RestClient restClient;
    private final HotelManagementClientProperties props;

    public ConfirmAvailabilityResponseDto confirmAvailability(UUID roomId, ConfirmAvailabilityRequestDto req) {
        String url = baseUrl() + "/rooms/" + roomId + "/confirm-availability";
        return postWithRetry(url, req, ConfirmAvailabilityResponseDto.class, req.requestId());
    }

    public void releaseHold(UUID roomId, ReleaseHoldRequestDto req) {
        String url = baseUrl() + "/rooms/" + roomId + "/release";
        postWithRetry(url, req, Void.class, req.requestId());
    }

    private String baseUrl() {
        String base = props.baseUrl();
        if (base == null) {
            return "";
        }
        // нормализуем хвост
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    private <T> T postWithRetry(String url, Object body, Class<T> respType, String requestId) {
        int attempts = Math.max(1, props.maxAttempts());
        Duration backoff = props.initialBackoff() == null ? Duration.ofMillis(200) : props.initialBackoff();

        RuntimeException last = null;
        for (int i = 1; i <= attempts; i++) {
            try {
                log.info("[requestId={}] hotel-management POST {} attempt {}/{}", requestId, url, i, attempts);

                return restClient.post()
                        .uri(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(h -> {
                            if (requestId != null && !requestId.isBlank()) {
                                h.set("X-Request-Id", requestId);
                            }
                        })
                        .body(body)
                        .retrieve()
                        .body(respType);

            } catch (ResourceAccessException e) {
                last = e;
                log.warn("[requestId={}] hotel-management network/timeout: {}", requestId, e.getMessage());
            } catch (RestClientResponseException e) {
                last = e;
                int code = e.getStatusCode().value();

                // Бизнес-ошибки (4xx кроме 429) ретраить не надо
                if (code >= 400 && code < 500 && code != 429) {
                    throw e;
                }

                log.warn("[requestId={}] hotel-management http status={} body={}", requestId, code, safeBody(e));
            }

            if (i < attempts) {
                sleep(backoff);
                backoff = backoff.multipliedBy(2);
            }
        }

        throw last;
    }

    private static void sleep(Duration d) {
        try {
            Thread.sleep(Math.max(0, d.toMillis()));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private static String safeBody(RestClientResponseException e) {
        try {
            return e.getResponseBodyAsString();
        } catch (Exception ex) {
            return "<unavailable>";
        }
    }
}

