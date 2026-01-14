package ru.larkin.bookingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.larkin.bookingservice.dto.req.RegisterUserRequest;
import ru.larkin.bookingservice.dto.req.UpdateUserRequest;
import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.service.UserService;

import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
//    @PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public Page<UserDtoResponse> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @PostMapping
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        UserDtoResponse created = userService.createByAdmin(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@NotNull @PathVariable("id") UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDtoResponse> updateUser(
            @NotNull @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        return ResponseEntity.ok(userService.update(id, updateUserRequest));
    }
}
