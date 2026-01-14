package ru.larkin.bookingservice.service.mapper;

import java.time.OffsetDateTime;

import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.persistence.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDtoResponse toResponse(User entity) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(entity.getId());
        userDtoResponse.setEmail(entity.getEmail());
        userDtoResponse.setName(entity.getUsername());
        userDtoResponse.setRole(entity.getRole().name());
        return userDtoResponse;
    }

    public static OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}
