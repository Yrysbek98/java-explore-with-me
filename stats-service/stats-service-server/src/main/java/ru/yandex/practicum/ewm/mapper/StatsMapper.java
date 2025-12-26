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

}
