package ru.larkin.hotelmanagementservice.dto.req;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ConfirmAvailabilityRequest(
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
        Integer holdMinutes
) {
}

