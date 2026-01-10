package ru.yandex.practicum.ewm.service;


import ru.yandex.practicum.ewm.dto.*;


import java.util.List;

public interface PrivateEventService {

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto dto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest request);
}
