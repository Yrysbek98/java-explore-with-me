package ru.yandex.practicum.ewm.events.mapper;


import ru.yandex.practicum.ewm.categories.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventShortDto;

import ru.yandex.practicum.ewm.events.dto.NewEventDto;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.users.mapper.UserMapper;

public class EventMapper {
  public static Event toEntityFromNewDto(NewEventDto dto, User user, Category category) {
        if (dto == null) return null;
        return new Event(
                dto.getAnnotation(),
                category,
                dto.getEventDate(),
                user,
                dto.getLocation(),
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                dto.getTitle());
    }

    public static EventShortDto toEventShortDto(Event event) {
        if (event == null) return null;
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
                );
    }

    public static EventFullDto toEventFullDto(Event event) {
        if (event == null) return null;
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.toDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }
}
