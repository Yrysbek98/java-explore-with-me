package ru.yandex.practicum.ewm.mapper;

import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.model.Stats;

public class StatsMapper {

    public static Stats toEntity(RequestStatsDto dto) {
        if (dto == null) {
            return null;
        }

        Stats booking = new Stats();
        booking.setApp(dto.getApp());
        booking.setIp(dto.getIp());
        booking.setUri(dto.getUri());
        booking.setTimestamp(dto.getTimestamp());
        return booking;
    }

    public static RequestStatsDto toDto(Stats stats) {
        if (stats == null) {
            return null;
        }

        return new RequestStatsDto(
                stats.getApp(),
                stats.getIp(),
                stats.getUri(),
                stats.getTimestamp()
        );
    }
}
