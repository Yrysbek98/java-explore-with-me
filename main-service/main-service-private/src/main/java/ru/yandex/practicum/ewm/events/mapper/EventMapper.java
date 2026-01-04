package ru.yandex.practicum.ewm.events.mapper;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.events.dto.EventShortDto;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.users.mapper.UserMapper;

public class EventMapper {
  /*  public static Event toEntity(CategoryDto dto) {
        if (dto == null) return null;
        return new Event(dto.getName());
    }*/

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
}
