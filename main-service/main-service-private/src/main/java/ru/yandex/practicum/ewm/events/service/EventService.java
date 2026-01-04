package ru.yandex.practicum.ewm.events.service;


import ru.yandex.practicum.ewm.events.dto.*;

import java.util.List;
import java.util.Optional;

public interface EventService {

    List<EventShortDto> getUsersEvents(Long userId);

    EventFullDto addNewEvent(Long userId, NewEventDto dto);

    EventFullDto getUsersEvent(Long userId, Long eventId);

    EventFullDto updateEvent(UpdateEventUserRequestDto dto );

    ParticipationRequestDto getUserEventRequest(Long userId, Long eventId);

    void updateUserEventRequest(Long userId, Long eventId); // ДОДЕЛАТЬ
}
