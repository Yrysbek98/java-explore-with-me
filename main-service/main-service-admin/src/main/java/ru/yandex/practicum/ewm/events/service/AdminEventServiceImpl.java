package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.EventDto.AdminEventSearchParams;
import ru.yandex.practicum.ewm.dto.EventDto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventDto.UpdateEventAdminRequest;
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
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    // private final StatisticsClient statisticsClient; // ← Для views (пока можем использовать 0L)

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEventsForAdmin(AdminEventSearchParams filter) {

        // Валидация дат
        if (filter.getRangeStart() != null && filter.getRangeEnd() != null) {
            if (filter.getRangeStart().isAfter(filter.getRangeEnd())) {
                throw new ValidationException("rangeStart не может быть позже rangeEnd");
            }
        }

        // Парсинг states
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
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));


        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
        }


        EventMapper.updateEventFromAdminRequest(event, dto, category);

        if (event.getEventDate() != null) {
            LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
            if (event.getEventDate().isBefore(oneHourFromNow)) {
                throw new ConflictException(
                        "Дата начала события должна быть не ранее чем за час от текущего момента"
                );
            }
        }

        if (dto.getStateAction() != null) {
            if ("PUBLISH_EVENT".equals(dto.getStateAction())) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException(
                            "Cannot publish the event because it's not in the right state: " + event.getState()
                    );
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());

            } else if ("REJECT_EVENT".equals(dto.getStateAction())) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException(
                            "Cannot reject the event because it's already published"
                    );
                }
                event.setState(EventState.CANCELED);
            }
        }

        Event saved = eventRepository.save(event);

        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Long views = 0L; // TODO: получить из сервиса статистики

        return EventMapper.toEventFullDto(saved, confirmedRequests, views);
    }


    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> results = requestRepository.countConfirmedRequestsByEventIds(eventIds);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // eventId
                        row -> (Long) row[1]   // count
                ));
    }

    private Map<Long, Long> getViewsMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        // TODO: Запрос к сервису статистики
        // Пока возвращаем пустую мапу (все views = 0)
        return eventIds.stream()
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> 0L
                ));
    }
}
