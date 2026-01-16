package ru.larkin.bookingservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;
import ru.larkin.bookingservice.client.HotelManagementClient;
import ru.larkin.bookingservice.domain.BookingStatus;
import ru.larkin.bookingservice.dto.req.ConfirmAvailabilityRequestDto;
import ru.larkin.bookingservice.dto.req.CreateBookingRequest;
import ru.larkin.bookingservice.dto.resp.ConfirmAvailabilityResponseDto;
import ru.larkin.bookingservice.persistence.entity.Booking;
import ru.larkin.bookingservice.persistence.entity.User;
import ru.larkin.bookingservice.persistence.repository.BookingRepository;
import ru.larkin.bookingservice.persistence.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class BookingSagaScenariosTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @MockBean
    HotelManagementClient hotelManagementClient;

    private User user() {
        User u = User.builder()
                .email("u" + UUID.randomUUID() + "@mail.test")
                .username("user")
                .passwordHash("hash")
                .role(ru.larkin.bookingservice.domain.UserRole.USER)
                .createdAt(OffsetDateTime.now())
                .build();
        return userRepository.save(u);
    }

    @Test
    void success_confirmed() {
        User u = user();
        String reqId = UUID.randomUUID().toString();

        when(hotelManagementClient.confirmAvailability(any(UUID.class), any(ConfirmAvailabilityRequestDto.class)))
                .thenReturn(new ConfirmAvailabilityResponseDto("token-1", OffsetDateTime.now().plusMinutes(10)));

        CreateBookingRequest req = CreateBookingRequest.builder()
                .requestId(reqId)
                .hotelId(UUID.randomUUID().toString())
                .roomId(UUID.randomUUID().toString())
                .from(OffsetDateTime.now().plusDays(1))
                .to(OffsetDateTime.now().plusDays(2))
                .build();

        var resp = bookingService.create(req, u.getId());
        Booking b = bookingRepository.findById(resp.getId()).orElseThrow();

        assertThat(b.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(b.getHoldToken()).isEqualTo("token-1");
    }

    @Test
    void remote409_conflict_cancelled_and_released_if_token_known() {
        User u = user();
        String reqId = UUID.randomUUID().toString();

        // эмулируем 409
        RestClientResponseException conflict = new RestClientResponseException(
                "conflict", 409, "CONFLICT", null, null, null);

        when(hotelManagementClient.confirmAvailability(any(UUID.class), any(ConfirmAvailabilityRequestDto.class)))
                .thenThrow(conflict);

        CreateBookingRequest req = CreateBookingRequest.builder()
                .requestId(reqId)
                .hotelId(UUID.randomUUID().toString())
                .roomId(UUID.randomUUID().toString())
                .from(OffsetDateTime.now().plusDays(1))
                .to(OffsetDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.create(req, u.getId()))
                .isInstanceOf(ru.larkin.bookingservice.service.exception.ConflictException.class);

        Optional<Booking> b = bookingRepository.findByRequestId(reqId);
        assertThat(b).isPresent();
        assertThat(b.get().getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void timeout_cancelled() {
        User u = user();
        String reqId = UUID.randomUUID().toString();

        when(hotelManagementClient.confirmAvailability(any(UUID.class), any(ConfirmAvailabilityRequestDto.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        CreateBookingRequest req = CreateBookingRequest.builder()
                .requestId(reqId)
                .hotelId(UUID.randomUUID().toString())
                .roomId(UUID.randomUUID().toString())
                .from(OffsetDateTime.now().plusDays(1))
                .to(OffsetDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.create(req, u.getId()))
                .isInstanceOf(ru.larkin.bookingservice.service.exception.ServiceUnavailableException.class);

        Booking b = bookingRepository.findByRequestId(reqId).orElseThrow();
        assertThat(b.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void redelivery_same_requestId_does_not_duplicate() {
        User u = user();
        String reqId = UUID.randomUUID().toString();

        when(hotelManagementClient.confirmAvailability(any(UUID.class), any(ConfirmAvailabilityRequestDto.class)))
                .thenReturn(new ConfirmAvailabilityResponseDto("token-1", OffsetDateTime.now().plusMinutes(10)));

        CreateBookingRequest req = CreateBookingRequest.builder()
                .requestId(reqId)
                .hotelId(UUID.randomUUID().toString())
                .roomId(UUID.randomUUID().toString())
                .from(OffsetDateTime.now().plusDays(1))
                .to(OffsetDateTime.now().plusDays(2))
                .build();

        var r1 = bookingService.create(req, u.getId());
        var r2 = bookingService.create(req, u.getId());

        assertThat(r2.getId()).isEqualTo(r1.getId());
        assertThat(bookingRepository.findByRequestId(reqId)).isPresent();

        verify(hotelManagementClient, times(1)).confirmAvailability(any(UUID.class), any(ConfirmAvailabilityRequestDto.class));
    }
}
