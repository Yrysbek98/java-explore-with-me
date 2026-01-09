package ru.yandex.practicum.ewm.requests.service;

import ru.yandex.practicum.ewm.dto.*;


import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
