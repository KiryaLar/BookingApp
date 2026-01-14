package ru.larkin.hotelmanagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.larkin.hotelmanagementservice.entity.Room;
import ru.larkin.hotelmanagementservice.repo.RoomRepository;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatController {

    private final RoomRepository roomRepository;

    @GetMapping("/rooms/popular")
    public List<Room> popularRooms() {
        return roomRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Room::getTimesBooked).reversed())
                .toList();
    }
}
