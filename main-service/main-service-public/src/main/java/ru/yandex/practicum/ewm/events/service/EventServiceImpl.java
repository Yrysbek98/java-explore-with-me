package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventSearchFilter;
import ru.yandex.practicum.ewm.events.dto.EventShortDto;
import ru.yandex.practicum.ewm.repository.EventRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;

    @Override
    public List<EventShortDto> getAllEvents(EventSearchFilter filter) {
        return List.of();
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
        return null;
    }
}
