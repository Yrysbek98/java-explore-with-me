package ru.yandex.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.ResponseStatsDto;
import ru.yandex.practicum.ewm.connection.StatsClient;
import ru.yandex.practicum.ewm.dto.*;
import ru.yandex.practicum.ewm.enums.EventState;

import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.exception.exceptionType.ValidationException;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> searchEventsForPublic(PublicEventSearchParams params,
                                                     HttpServletRequest request) {

        saveHit(request);

        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new ValidationException("Проблемы с датами");
            }
        }

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }


        Sort sort = Sort.by("eventDate").ascending(); // по умолчанию
        if (params.getSort() != null) {
            if (params.getSort().equalsIgnoreCase("VIEWS")) {
                sort = Sort.by("id").ascending();
            } else if (params.getSort().equalsIgnoreCase("EVENT_DATE")) {
                sort = Sort.by("eventDate").ascending();
            }
        }

        PageRequest pageable = PageRequest.of(
                params.getFrom() / params.getSize(),
                params.getSize(),
                sort
        );

        Page<Event> events = eventRepository.searchEventsForPublic(
                params.getText(),
                params.getCategories(),
                params.getPaid(),
                rangeStart,
                rangeEnd,
                params.getOnlyAvailable(),
                pageable
        );

        List<Long> eventIds = events.getContent().stream()
                .map(Event::getId)
                .collect(Collectors.toList());


        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);


        Map<Long, Long> viewsMap = getViewsMap(eventIds);

        List<EventShortDto> result = events.getContent().stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L),
                        viewsMap.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
        if (params.getSort() != null && params.getSort().equalsIgnoreCase("VIEWS")) {
            result.sort((e1, e2) -> Long.compare(e2.getViews(), e1.getViews()));
        }

        return result;
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {

        saveHit(request);

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));


        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);


        Long views = getViewsForSingleEvent(eventId);


        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    private void saveHit(HttpServletRequest request) {
        try {
            RequestStatsDto dto = RequestStatsDto.builder()
                    .app("ewm-main-service")
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build();

            statsClient.saveHit(dto);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения статистики: " + e.getMessage());
        }
    }

    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> results = requestRepository.countConfirmedRequestsByEventIds(eventIds);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    private Map<Long, Long> getViewsMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        try {
            List<String> uris = eventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());

            ResponseStatsDto[] stats = statsClient.getStats(
                    LocalDateTime.now().minusYears(10),
                    LocalDateTime.now(),
                    uris,
                    false
            ).getBody();

            if (stats != null && stats.length > 0) {
                return java.util.Arrays.stream(stats)
                        .collect(Collectors.toMap(
                                stat -> extractEventIdFromUri(stat.getUri()),
                                ResponseStatsDto::getHits
                        ));
            }
        } catch (Exception e) {
            System.err.println("Ошибка получения статистики: " + e.getMessage());
        }

        return eventIds.stream()
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> 0L
                ));
    }

    private Long getViewsForSingleEvent(Long eventId) {
        try {
            List<String> uris = List.of("/events/" + eventId);

            ResponseStatsDto[] stats = statsClient.getStats(
                    LocalDateTime.now().minusYears(10),
                    LocalDateTime.now().plusSeconds(1),
                    uris,
                    false
            ).getBody();

            if (stats != null && stats.length > 0) {
                return stats[0].getHits();
            }
        } catch (Exception e) {
            System.err.println("Ошибка получения статистики для события " + eventId + ": " + e.getMessage());
        }

        return 0L;
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
