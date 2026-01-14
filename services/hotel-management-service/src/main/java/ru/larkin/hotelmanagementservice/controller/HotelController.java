package ru.larkin.hotelmanagementservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.larkin.hotelmanagementservice.dto.req.CreateHotelRequest;
import ru.larkin.hotelmanagementservice.dto.resp.HotelResponse;
import ru.larkin.hotelmanagementservice.service.HotelService;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasRole('ADMIN')")
    public HotelResponse createHotel(@Valid @RequestBody CreateHotelRequest request) {
        return hotelService.create(request);
    }

    @GetMapping
//    @PreAuthorize("hasRole('USER'||'ADMIN')")
    public List<HotelResponse> listHotels() {
        return hotelService.getHotels();
    }
}

