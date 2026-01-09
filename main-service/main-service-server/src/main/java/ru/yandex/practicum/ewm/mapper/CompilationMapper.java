package ru.yandex.practicum.ewm.mapper;

import ru.yandex.practicum.ewm.dto.CompilationDto;
import ru.yandex.practicum.ewm.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.dto.EventShortDto;
import ru.yandex.practicum.ewm.model.Compilation;
import ru.yandex.practicum.ewm.model.Event;

import java.util.*;
import java.util.stream.Collectors;

public class CompilationMapper {

    private CompilationMapper() {
        throw new IllegalStateException("Utility class");
    }


    public static Compilation toEntity(NewCompilationDto dto, Set<Event> events) {
        if (dto == null) return null;

        return Compilation.builder()
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .title(dto.getTitle())
                .events(events != null ? events : new HashSet<>())
                .build();
    }


    public static CompilationDto toDto(Compilation compilation,
                                       Map<Long, Long> confirmedRequestsMap,
                                       Map<Long, Long> viewsMap) {
        if (compilation == null) return null;

        List<EventShortDto> eventDtos = compilation.getEvents().stream()
                .sorted(Comparator.comparing(Event::getId)) // Сортируем для стабильности
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L),
                        viewsMap.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventDtos)
                .build();
    }


    public static void updateCompilationFromDto(Compilation compilation,
                                                UpdateCompilationRequest dto,
                                                Set<Event> events) {
        if (dto == null) return;

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (events != null) {
            compilation.setEvents(events);
        }
    }
}
