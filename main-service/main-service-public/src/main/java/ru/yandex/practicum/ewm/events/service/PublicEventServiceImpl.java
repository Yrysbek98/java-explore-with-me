package ru.yandex.practicum.ewm.events.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.EventDto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventDto.EventShortDto;
import ru.yandex.practicum.ewm.dto.EventDto.PublicEventSearchParams;
import ru.yandex.practicum.ewm.enums.EventState;

import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
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
    // private final StatisticsClient statisticsClient; // TODO: добавить когда будет готов


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> searchEventsForPublic(PublicEventSearchParams params,
                                                     HttpServletRequest request) {

        LocalDateTime rangeStart = params.getRangeStart();
        LocalDateTime rangeEnd = params.getRangeEnd();

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }


        Sort sort = Sort.by("eventDate").ascending(); // по умолчанию
        if (params.getSort() != null) {
            if (params.getSort().equalsIgnoreCase("VIEWS")) {
                sort = Sort.by("id").ascending(); // Временно по ID, т.к. views не в БД
                // TODO: После интеграции со статистикой сортировать по views
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

        // Batch получение views из сервиса статистики
        Map<Long, Long> viewsMap = getViewsMap(eventIds);

        // Сохраняем статистику просмотра
        // TODO: statisticsClient.saveHit(request.getRequestURI(), request.getRemoteAddr());

        return events.getContent().stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L),
                        viewsMap.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {
        // Получаем только опубликованные события
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        // Получаем confirmedRequests
        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);

        // Получаем views из статистики и увеличиваем счетчик
        // TODO: Long views = statisticsClient.getViewsAndIncrement("/events/" + eventId);
        Long views = 0L; // Временно

        // Сохраняем статистику просмотра
        // TODO: statisticsClient.saveHit(request.getRequestURI(), request.getRemoteAddr());

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    // =================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===================

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

        // TODO: Запрос к сервису статистики
        // Map<Long, Long> viewsMap = statisticsClient.getViewsForEvents(eventIds);

        // Временная заглушка - возвращаем 0 для всех
        return eventIds.stream()
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> 0L
                ));
    }
}
