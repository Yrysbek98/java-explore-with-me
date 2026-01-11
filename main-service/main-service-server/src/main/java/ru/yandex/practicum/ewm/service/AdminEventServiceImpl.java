package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.ResponseStatsDto;
import ru.yandex.practicum.ewm.connection.StatsClient;
import ru.yandex.practicum.ewm.dto.AdminEventSearchParams;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.exception.exceptionType.ValidationException;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEventsForAdmin(AdminEventSearchParams filter) {

        if (filter.getRangeStart() != null && filter.getRangeEnd() != null) {
            if (filter.getRangeStart().isAfter(filter.getRangeEnd())) {
                throw new ValidationException("Проблемы с датами");
            }
        }

        List<EventState> eventStates = null;
        if (filter.getStates() != null && !filter.getStates().isEmpty()) {
            eventStates = filter.getStates().stream()
                    .map(state -> {
                        try {
                            return EventState.valueOf(state.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            throw new ValidationException("Неизвестное состояние события: " + state);
                        }
                    })
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(filter.getFrom() / filter.getSize(), filter.getSize());

        Page<Event> events = eventRepository.searchEvents(
                filter.getUsers(),
                eventStates,
                filter.getCategories(),
                filter.getRangeStart(),
                filter.getRangeEnd(),
                pageable
        );

        List<Long> eventIds = events.getContent().stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        Map<Long, Long> viewsMap = getViewsMap(eventIds);

        return events.getContent().stream()
                .map(event -> EventMapper.toEventFullDto(
                        event,
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L),
                        viewsMap.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с таким id=" + eventId + " не найден"));


        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категорие с таким id=" + dto.getCategory() + " не найден"));
        }


        EventMapper.updateEventFromAdminRequest(event, dto, category);

        if (event.getEventDate() != null) {
            LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
            if (event.getEventDate().isBefore(oneHourFromNow)) {
                throw new ValidationException(
                        "Дата начала события должна быть не ранее чем за час от текущего момента"
                );
            }
        }

        if (dto.getStateAction() != null) {
            if ("PUBLISH_EVENT".equals(dto.getStateAction())) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException(
                            "Невозможно опубликовать событие, поскольку оно находится в неправильном состоянии:" + event.getState()
                    );
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());

            } else if ("REJECT_EVENT".equals(dto.getStateAction())) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException(
                            "Отклонить событие невозможно, так как оно уже опубликовано."
                    );
                }
                event.setState(EventState.CANCELED);
            }
        }

        Event saved = eventRepository.save(event);

        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Long views = getViewsForSingleEvent(eventId);

        return EventMapper.toEventFullDto(saved, confirmedRequests, views);
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
                    LocalDateTime.now().minusYears(10), // Начало времени
                    LocalDateTime.now(),
                    uris,
                    true
            ).getBody();

            if (stats != null && stats.length > 0) {
                return java.util.Arrays.stream(stats)
                        .collect(Collectors.toMap(
                                stat -> extractEventIdFromUri(stat.getUri()),
                                ResponseStatsDto::getHits
                        ));
            }
        } catch (Exception e) {
            log.error("Ошибка получения статистики для подборки", e);
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
                    true
            ).getBody();

            if (stats != null && stats.length > 0) {
                return stats[0].getHits();
            }
        } catch (Exception e) {
            log.error("Ошибка получения статистики для подборки", e);
        }

        return 0L;
    }

    private Long extractEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
