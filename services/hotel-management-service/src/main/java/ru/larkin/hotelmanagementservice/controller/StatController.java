package ru.larkin.hotelmanagementservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.larkin.hotelmanagementservice.dto.resp.RoomLoadStatResponse;
import ru.larkin.hotelmanagementservice.entity.Room;
import ru.larkin.hotelmanagementservice.service.RoomService;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatController {

    private final RoomService roomService;

    /**
     * Статистика загруженности номеров.
     * По умолчанию: самые популярные (timesBooked DESC).
     *
     * @param order "desc" (по умолчанию) или "asc"
     * @param top   ограничение на количество записей (опционально)
     */
    @GetMapping("/rooms/load")
    public ResponseEntity<List<RoomLoadStatResponse>> roomLoadStats(
            @RequestParam(name = "order", required = false, defaultValue = "desc") String order,
            @RequestParam(name = "top", required = false) Integer top
    ) {
        return ResponseEntity.ok(roomService.getRoomsLoadStat(order, top));
    }
}
