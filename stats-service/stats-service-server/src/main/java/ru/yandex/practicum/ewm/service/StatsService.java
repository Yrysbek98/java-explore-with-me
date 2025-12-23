package ru.yandex.practicum.ewm.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsService {

    void saveHit(RequestStatsDto dto);

    List<ResponseStatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    );
}
