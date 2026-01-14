package ru.larkin.bookingservice.service.mapper;

import java.time.OffsetDateTime;

import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.persistence.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDtoResponse toApi(User entity) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(entity.getId());
        userDtoResponse.setEmail(entity.getEmail());
        userDtoResponse.setName(entity.getUsername());
        userDtoResponse.setRole(UserDtoResponse.RoleEnum.valueOf(entity.getRole().name()));
        userDtoResponse.setCreatedAt(entity.getCreatedAt());
        return userDtoResponse;
    }

    public static OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}
