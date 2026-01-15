package ru.larkin.bookingservice.service.mapper;

import lombok.experimental.UtilityClass;
import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.persistence.entity.User;

@UtilityClass
public final class UserMapper {

    public static UserDtoResponse toResponse(User entity) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(entity.getId());
        userDtoResponse.setEmail(entity.getEmail());
        userDtoResponse.setUsername(entity.getUsername());
        userDtoResponse.setRole(entity.getRole().name());
        return userDtoResponse;
    }
}
