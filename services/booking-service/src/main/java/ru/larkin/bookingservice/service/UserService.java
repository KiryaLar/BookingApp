package ru.larkin.bookingservice.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.larkin.bookingservice.domain.UserRole;
import ru.larkin.bookingservice.dto.req.AuthRequest;
import ru.larkin.bookingservice.dto.req.RegisterUserRequest;
import ru.larkin.bookingservice.dto.req.UpdateUserRequest;
import ru.larkin.bookingservice.dto.resp.AccessTokenResponse;
import ru.larkin.bookingservice.dto.resp.UserDtoResponse;
import ru.larkin.bookingservice.persistence.entity.User;
import ru.larkin.bookingservice.persistence.repository.UserRepository;
import ru.larkin.bookingservice.service.exception.ConflictException;
import ru.larkin.bookingservice.service.exception.NotFoundException;
import ru.larkin.bookingservice.service.exception.UnauthorizedException;
import ru.larkin.bookingservice.service.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int ACCESS_TOKEN_TTL_SECONDS = 3600;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Transactional
    public UserDtoResponse register(RegisterUserRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }

        User entity = User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(UserRole.USER)
                .build();

        userRepository.save(entity);
        return UserMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public AccessTokenResponse authorize(AuthRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Неверный email"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Неверный пароль");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ACCESS_TOKEN_TTL_SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("booking-service")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                // то, что ожидает BookingSecurityConfig: role/roles
                .claim("role", user.getRole().name())
                .claim("roles", List.of(user.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        AccessTokenResponse resp = new AccessTokenResponse();
        resp.setAccessToken(token);
        resp.setExpiresIn(ACCESS_TOKEN_TTL_SECONDS);
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

        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            entity.setUsername(req.getUsername());
        }

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }

        if (req.getRole() != null) {
            entity.setRole(UserRole.valueOf(req.getRole().name()));
        }

        userRepository.save(entity);
        return UserMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<UserDtoResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toResponse);
    }
}
