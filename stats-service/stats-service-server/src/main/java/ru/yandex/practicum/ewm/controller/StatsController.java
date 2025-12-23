package ru.yandex.practicum.ewm.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Object> addNewUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody RequestStatsDto dto
    ) {
        return statsService.addNewData(userId,dto );
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getAllEvents(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody RequestStatsDto dto,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end
    ) {
        return statsService.getData(userId, dto, start, end);
    }

}
