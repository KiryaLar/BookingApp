package ru.larkin.bookingservice.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoResponse {

    private UUID id;
    private String username;
    private String email;
    private String name;
    private String role;
}

