package ru.larkin.bookingservice.dto.resp;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccessTokenResponse {

    @NotNull
    private String accessToken;
    private Integer expiresIn;
}

