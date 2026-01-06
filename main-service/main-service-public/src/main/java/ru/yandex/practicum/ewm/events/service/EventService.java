package ru.yandex.practicum.ewm.events.service;

import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventSearchFilter;
import ru.yandex.practicum.ewm.events.dto.EventShortDto;

import java.util.List;

public interface EventService {

    List<EventShortDto> getAllEvents(EventSearchFilter filter);

    EventFullDto getEventById(Long eventId);
}
