package ru.larkin.bookingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.larkin.bookingservice.dto.req.AuthRequest;
import ru.larkin.bookingservice.dto.resp.AuthResponse;
import ru.larkin.bookingservice.dto.req.RegisterUserRequest;
import ru.larkin.bookingservice.dto.req.UpdateUserRequest;
import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/auth", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthResponse> authorizeUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(userService.authorize(authRequest));
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        UserDtoResponse created = userService.createByAdmin(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@NotNull @PathVariable("id") UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDtoResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        UserDtoResponse created = userService.register(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDtoResponse> updateUser(
            @NotNull @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        return ResponseEntity.ok(userService.update(id, updateUserRequest));
    }
}

