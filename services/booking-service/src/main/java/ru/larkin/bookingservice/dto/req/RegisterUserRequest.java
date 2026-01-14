package ru.larkin.bookingservice.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;

    @NotNull
    @Size(min = 1)
    private String name;
}

