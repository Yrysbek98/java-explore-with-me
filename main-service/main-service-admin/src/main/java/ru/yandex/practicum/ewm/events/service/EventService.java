package ru.yandex.practicum.ewm.events.service;

import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventSearchFilter;
import ru.yandex.practicum.ewm.events.dto.UpdateEventDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEvents(
            EventSearchFilter filter);

    EventFullDto updateEvent(Long eventId, UpdateEventDto dto);
}

