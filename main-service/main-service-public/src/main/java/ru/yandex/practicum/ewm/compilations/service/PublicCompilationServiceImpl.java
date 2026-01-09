package ru.yandex.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.CompilationDto.CompilationDto;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.model.Compilation;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CompilationRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Compilation> compilations = compilationRepository.findByPinnedFilter(pinned, pageable);


        Set<Event> allEvents = compilations.getContent().stream()
                .flatMap(comp -> comp.getEvents().stream())
                .collect(Collectors.toSet());


        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(allEvents);
        Map<Long, Long> viewsMap = getViewsMap(allEvents);

        return compilations.getContent().stream()
                .map(comp -> CompilationMapper.toDto(comp, confirmedRequestsMap, viewsMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с таким id=" + compId + " не найден"));


        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(compilation.getEvents());
        Map<Long, Long> viewsMap = getViewsMap(compilation.getEvents());

        return CompilationMapper.toDto(compilation, confirmedRequestsMap, viewsMap);
    }

    private Map<Long, Long> getConfirmedRequestsMap(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Object[]> results = requestRepository.countConfirmedRequestsByEventIds(eventIds);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    private Map<Long, Long> getViewsMap(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        // TODO: Запрос к сервису статистики


        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> 0L
                ));
    }
}
