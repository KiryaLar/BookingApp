package ru.larkin.bookingservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.larkin.bookingservice.domain.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoResponse {

    private UUID id;
    private String email;
    private String name;
    private UserRole role;
    private OffsetDateTime createdAt;
}

