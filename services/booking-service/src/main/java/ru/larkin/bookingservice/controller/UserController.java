package ru.larkin.bookingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.larkin.bookingservice.dto.req.AuthRequest;
import ru.larkin.bookingservice.dto.resp.AuthResponse;
import ru.larkin.bookingservice.dto.req.RegisterUserRequest;
import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authorizeUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(userService.authorize(authRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDtoResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        UserDtoResponse created = userService.register(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
