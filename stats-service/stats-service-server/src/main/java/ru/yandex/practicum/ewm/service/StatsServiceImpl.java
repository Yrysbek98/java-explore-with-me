package ru.yandex.practicum.ewm.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.ewm.RequestStatsDto;

import java.time.LocalDateTime;

public class StatsServiceImpl implements StatsService{
    @Override
    public  ResponseEntity<Object> addNewData(Long userId, RequestStatsDto dto) {
return  null;
    }

    @Override
    public ResponseEntity<Object> getData(Long userId, RequestStatsDto dto, LocalDateTime start, LocalDateTime end) {
return  null;
    }
}
