package ru.yandex.practicum.ewm.mapper;

import ru.yandex.practicum.ewm.dto.LocationDto.LocationDto;
import ru.yandex.practicum.ewm.model.Location;

public class LocationMapper {

    public static Location toEntity(LocationDto dto) {
        if (dto == null) return null;
        return Location.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }

    public static LocationDto toDto(Location location) {
        if (location == null) return null;
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
