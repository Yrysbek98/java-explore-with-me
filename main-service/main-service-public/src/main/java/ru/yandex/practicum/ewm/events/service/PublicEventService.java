package ru.yandex.practicum.ewm.events.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.ewm.dto.EventDto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventDto.EventShortDto;
import ru.yandex.practicum.ewm.dto.EventDto.PublicEventSearchParams;

import java.util.List;

public interface PublicEventService {
    List<EventShortDto> searchEventsForPublic(PublicEventSearchParams params, HttpServletRequest request);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);
}
