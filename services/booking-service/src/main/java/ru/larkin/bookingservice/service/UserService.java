package ru.larkin.bookingservice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.larkin.bookingservice.domain.UserRole;
import ru.larkin.bookingservice.dto.req.AuthRequest;
import ru.larkin.bookingservice.dto.resp.AuthResponse;
import ru.larkin.bookingservice.dto.req.RegisterUserRequest;
import ru.larkin.bookingservice.dto.req.UpdateUserRequest;
import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.persistence.entity.User;
import ru.larkin.bookingservice.persistence.repository.UserRepository;
import ru.larkin.bookingservice.service.exception.ConflictException;
import ru.larkin.bookingservice.service.exception.NotFoundException;
import ru.larkin.bookingservice.service.exception.UnauthorizedException;
import ru.larkin.bookingservice.service.mapper.UserMapper;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDtoResponse register(RegisterUserRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }

        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        String passwordHash = "{noop}" + req.getPassword();

        User entity = User.builder()
                .id(id)
                .email(req.getEmail())
                .username(req.getName())
                .passwordHash(passwordHash)
                .role(UserRole.USER)
                .createdAt(now)
                .build();

        userRepository.save(entity);
        return UserMapper.toResponse(entity);
    }

    @Transactional
    public UserDtoResponse createByAdmin(RegisterUserRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }

        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        String passwordHash = "{noop}" + req.getPassword();

        User entity = User.builder()
                .id(id)
                .email(req.getEmail())
                .username(req.getName())
                .passwordHash(passwordHash)
                .role(UserRole.USER)
                .createdAt(now)
                .build();

        userRepository.save(entity);
        return UserMapper.toResponse(entity);
    }

    @Transactional
    public AuthResponse authorize(AuthRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Неверный email или пароль"));

        if (!user.getPasswordHash().equals("{noop}" + req.getPassword())) {
            throw new UnauthorizedException("Неверный email или пароль");
        }

        AuthResponse resp = new AuthResponse();
        resp.setAccessToken("stub-token-for-" + user.getId());
        resp.setTokenType(AuthResponse.TokenTypeEnum.BEARER);
        resp.setExpiresIn(3600);
        return resp;
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDtoResponse update(UUID id, UpdateUserRequest req) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            boolean emailTaken = userRepository.findByEmailIgnoreCase(req.getEmail())
                    .filter(u -> !u.getId().equals(id))
                    .isPresent();
            if (emailTaken) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
            entity.setEmail(req.getEmail());
        }

        // username обновляется из request.username
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            entity.setUsername(req.getUsername());
        }

//        TODO: Шифрование пароля
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            entity.setPasswordHash("{noop}" + req.getPassword());
        }

        if (req.getRole() != null) {
            entity.setRole(UserRole.valueOf(req.getRole().name()));
        }

        userRepository.save(entity);
        return UserMapper.toResponse(entity);
    }

    public Page<UserDtoResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toResponse);
    }
}
