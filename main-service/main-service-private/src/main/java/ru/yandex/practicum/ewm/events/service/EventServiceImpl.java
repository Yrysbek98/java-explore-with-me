package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.events.dto.*;
import ru.yandex.practicum.ewm.events.mapper.EventMapper;
import ru.yandex.practicum.ewm.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public List<EventShortDto> getUsersEvents(Long userId) {
        return eventRepository.findByInitiator_idOrderByStartDesc(userId)
                .stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto dto) {
        return null;
    }

    @Override
    public EventFullDto getUsersEvent(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventFullDto updateEvent(UpdateEventUserRequestDto dto) {
        return null;
    }

    @Override
    public ParticipationRequestDto getUserEventRequest(Long userId, Long eventId) {
        return null;
    }

    @Override
    public void updateUserEventRequest(Long userId, Long eventId) {

    }
}
