package ru.yandex.practicum.ewm.requests.service;

import ru.yandex.practicum.ewm.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getUsersRequests(Long userId);

    RequestDto addNewRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);
}
