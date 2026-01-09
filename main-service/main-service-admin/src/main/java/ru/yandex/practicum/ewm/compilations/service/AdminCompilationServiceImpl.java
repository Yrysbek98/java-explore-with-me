package ru.yandex.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.ResponseStatsDto;
import ru.yandex.practicum.ewm.connection.StatsClient;
import ru.yandex.practicum.ewm.dto.CompilationDto.CompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationDto.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationDto.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.model.Compilation;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CompilationRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;


    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {

        if (compilationRepository.existsByTitle(dto.getTitle())) {
            throw new ConflictException("Не удалось найти данную подборку");
        }


        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));


            if (events.size() != dto.getEvents().size()) {
                throw new NotFoundException("Некоторые события не найдены");
            }
        }


        Compilation compilation = CompilationMapper.toEntity(dto, events);
        Compilation saved = compilationRepository.save(compilation);


        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(events);
        Map<Long, Long> viewsMap = getViewsMap(events);

        return CompilationMapper.toDto(saved, confirmedRequestsMap, viewsMap);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Данная подборка =" + compId + " не найден");
        }

        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Данная подборка " + compId + " не найдена"));


        if (request.getTitle() != null &&
                !request.getTitle().equals(compilation.getTitle()) &&
                compilationRepository.existsByTitleAndIdNot(request.getTitle(), compId)) {
            throw new ConflictException(
                    "Подборка  '" + request.getTitle() + "' уже достигло лимита"
            );
        }


        Set<Event> events = null;
        if (request.getEvents() != null) {
            if (request.getEvents().isEmpty()) {

                events = new HashSet<>();
            } else {
                events = new HashSet<>(eventRepository.findAllById(request.getEvents()));


                if (events.size() != request.getEvents().size()) {
                    throw new NotFoundException("Некоторые события не найдены");
                }
            }
        }


        CompilationMapper.updateCompilationFromDto(compilation, request, events);
        Compilation updated = compilationRepository.save(compilation);


        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(updated.getEvents());
        Map<Long, Long> viewsMap = getViewsMap(updated.getEvents());

        return CompilationMapper.toDto(updated, confirmedRequestsMap, viewsMap);
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

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        try {
            List<String> uris = eventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());

            ResponseStatsDto[] stats = statsClient.getStats(
                    LocalDateTime.now().minusYears(10), // Начало времени
                    LocalDateTime.now(),
                    uris,
                    false
            ).getBody();

            if (stats != null && stats.length > 0) {
                return Arrays.stream(stats)
                        .collect(Collectors.toMap(
                                stat -> extractEventIdFromUri(stat.getUri()),
                                ResponseStatsDto::getHits
                        ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка получения статистики для подборки: " + e.getMessage());
        }

        return eventIds.stream()
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> 0L
                ));
    }

    private Long extractEventIdFromUri(String uri) {
        // uri формата "/events/123"
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
