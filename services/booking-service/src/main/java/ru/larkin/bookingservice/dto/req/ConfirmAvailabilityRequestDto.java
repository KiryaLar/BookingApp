package ru.larkin.bookingservice.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConfirmAvailabilityRequestDto(
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
        String requestId,
        Integer holdMinutes
) {
}

