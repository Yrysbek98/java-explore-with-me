package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.UpdateEventDto;
import ru.yandex.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private  final EventRepository eventRepository;

    @Override
    public List<EventFullDto> getAllEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size) {
        return List.of();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventDto dto) {
        return null;
    }
}
