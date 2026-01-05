package ru.yandex.practicum.ewm.events.service;


import ru.yandex.practicum.ewm.events.dto.*;

import java.util.List;
import java.util.Optional;

public interface EventService {

    List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto addNewEvent(Long userId, NewEventDto dto);

    EventFullDto getUsersEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto dto );

    ParticipationRequestDto getUserEventRequest(Long userId, Long eventId);

    void updateUserEventRequest(Long userId, Long eventId); // ДОДЕЛАТЬ
}
