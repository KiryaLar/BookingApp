package ru.larkin.bookingservice.contoller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.larkin.bookingservice.api.UsersApi;
import ru.larkin.bookingservice.model.AuthRequest;
import ru.larkin.bookingservice.model.AuthResponse;
import ru.larkin.bookingservice.model.RegisterUserRequest;
import ru.larkin.bookingservice.model.UpdateUserRequest;
import ru.larkin.bookingservice.model.User;

@RestController
public class UserController implements UsersApi {

    @Override
    public ResponseEntity<AuthResponse> authorizeUser(@Valid AuthRequest authRequest) {
        return UsersApi.super.authorizeUser(authRequest);
    }

    @Override
    public ResponseEntity<User> createUser(@Valid RegisterUserRequest registerUserRequest) {
        return UsersApi.super.createUser(registerUserRequest);
    }

    @Override
    public ResponseEntity<Void> deleteUser(@NotNull UUID id) {
        return UsersApi.super.deleteUser(id);
    }

    @Override
    public ResponseEntity<User> registerUser(@Valid RegisterUserRequest registerUserRequest) {
        return UsersApi.super.registerUser(registerUserRequest);
    }

    @Override
    public ResponseEntity<User> updateUser(@NotNull UUID id, @Valid UpdateUserRequest updateUserRequest) {
        return UsersApi.super.updateUser(id, updateUserRequest);
    }
}
