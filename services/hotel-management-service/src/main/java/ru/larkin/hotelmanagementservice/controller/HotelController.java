package ru.larkin.hotelmanagementservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.larkin.hotelmanagementservice.dto.req.CreateHotelRequest;
import ru.larkin.hotelmanagementservice.dto.resp.HotelResponse;
import ru.larkin.hotelmanagementservice.service.HotelService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody CreateHotelRequest request) {
        HotelResponse createdHotel = hotelService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdHotel.id())
                .toUri();
        return ResponseEntity.created(location).body(createdHotel);
    }

    @GetMapping
//    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<List<HotelResponse>> listHotels() {
        return ResponseEntity.ok(hotelService.getHotels());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }
}

