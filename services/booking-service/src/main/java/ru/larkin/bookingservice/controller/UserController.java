package ru.larkin.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Регистрация и авторизация")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth")
    @Operation(summary = "Авторизация", description = "Аутентификация пользователя и выдача JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная авторизация",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = @Content)
    })
    public ResponseEntity<AuthResponse> authorizeUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(userService.authorize(authRequest));
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация", description = "Регистрация пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован",
                    content = @Content(schema = @Schema(implementation = UserDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует", content = @Content)
    })
    public ResponseEntity<UserDtoResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        UserDtoResponse created = userService.register(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
