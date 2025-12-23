package ru.yandex.practicum.ewm.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.ewm.RequestStatsDto;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StatsService {

    ResponseEntity<Object> addNewData(Long userId, RequestStatsDto dto);

    ResponseEntity<Object> getData(Long userId, RequestStatsDto dto, LocalDateTime start, LocalDateTime end);
}
