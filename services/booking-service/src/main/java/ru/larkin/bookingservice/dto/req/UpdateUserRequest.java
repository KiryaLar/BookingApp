package ru.larkin.bookingservice.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.larkin.bookingservice.domain.UserRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 1)
    private String username;

    @Email
    private String email;

    @Size(min = 8)
    private String password;

    private UserRole role;
}

