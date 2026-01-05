package ru.yandex.practicum.ewm.events.service;


import ru.yandex.practicum.ewm.events.dto.*;

import java.util.List;

public interface EventService {

    List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto addNewEvent(Long userId, UpdateEventDto dto);

    EventFullDto getUsersEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto dto );

    ParticipationRequestDto getUserEventRequest(Long userId, Long eventId);

    void updateUserEventRequest(Long userId, Long eventId); // ДОДЕЛАТЬ
}
