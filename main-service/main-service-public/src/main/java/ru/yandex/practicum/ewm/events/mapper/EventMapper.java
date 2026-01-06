package ru.yandex.practicum.ewm.events.mapper;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.ewm.categories.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventShortDto;
import ru.yandex.practicum.ewm.events.dto.NewEventDto;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.Location;
import ru.yandex.practicum.ewm.users.mapper.UserMapper;
@RequiredArgsConstructor
public class EventMapper {
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
   // private final RequestRepository requestRepository;

    public EventShortDto toShortDto(Event event) {
        if (event == null) {
            return null;
        }

        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .initiator(userMapper.toShortDto(event.getInitiator()))
                .views(event.getViews() != null ? event.getViews() : 0L)
                //.confirmedRequests(getConfirmedRequestsCount(event.getId()))
                .build();
    }

    public EventFullDto toFullDto(Event event) {
        if (event == null) {
            return null;
        }

        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(categoryMapper.toDto(event.getCategory()))
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .location(Location.builder()
                        .lat(event.getLocation().getLat())
                        .lon(event.getLocation().getLon())
                        .build())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .initiator(userMapper.toShortDto(event.getInitiator()))
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .views(event.getViews() != null ? event.getViews() : 0L)
              //  .confirmedRequests(getConfirmedRequestsCount(event.getId()))
                .build();
    }





}
