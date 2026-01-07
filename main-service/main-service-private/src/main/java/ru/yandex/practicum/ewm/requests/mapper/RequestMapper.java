package ru.yandex.practicum.ewm.requests.mapper;


import ru.yandex.practicum.ewm.model.Request;
import ru.yandex.practicum.ewm.requests.dto.RequestDto;


public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        if (request == null) return null;

        return RequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }
}
