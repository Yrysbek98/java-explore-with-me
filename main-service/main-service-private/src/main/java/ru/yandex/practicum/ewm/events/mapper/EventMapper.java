package ru.yandex.practicum.ewm.events.mapper;


import ru.yandex.practicum.ewm.categories.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventShortDto;

import ru.yandex.practicum.ewm.events.dto.NewEventDto;
import ru.yandex.practicum.ewm.events.dto.UpdateEventUserRequestDto;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.users.mapper.UserMapper;

public class EventMapper {
    public static Event toEntityFromNewDto(NewEventDto dto, User user, Category category) {
        if (dto == null) return null;

        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .eventDate(dto.getEventDate())
                .initiator(user)
                .location(dto.getLocation())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .state(EventState.PENDING)
                .build();
    }

    public static Event toEntityFromUpdateDto(Long eventId, UpdateEventUserRequestDto dto, User user, Category category) {
        if (dto == null) return null;

        return Event.builder()
                .id(eventId)
                .annotation(dto.getAnnotation())
                .category(category)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .initiator(user)
                .location(dto.getLocation())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .state(EventState.PENDING)
                .title(dto.getTitle())
                .build();
    }
    public static EventShortDto toEventShortDto(Event event) {
        if (event == null) return null;

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
    public static EventFullDto toEventFullDto(Event event) {
        if (event == null) return null;

        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }


}
