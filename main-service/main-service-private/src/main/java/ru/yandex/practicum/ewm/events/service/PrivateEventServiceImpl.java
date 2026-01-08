package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.EventDto.*;
import ru.yandex.practicum.ewm.dto.RequestDto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.User;
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
public class PrivateEventServiceImpl implements PrivateEventService{
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;



    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> eventPage = eventRepository.findByInitiatorId(userId, pageable);


        List<Long> eventIds = eventPage.getContent().stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);
        Map<Long, Long> viewsMap = getViewsMap(eventIds);

        return eventPage.getContent().stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L),
                        viewsMap.getOrDefault(event.getId(), 0L)
                ))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким " + userId + " не найден"));


        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с таким" + dto.getCategory() + " не найден"));


        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + dto.getEventDate()
            );
        }

        Event event = EventMapper.toEntity(dto, user, category);
        Event saved = eventRepository.save(event);


        return EventMapper.toEventFullDto(saved, 0L, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Long views = 0L; // TODO: получить из сервиса статистики

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }


        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
        }

        EventMapper.updateEventFromUserRequest(event, dto, category);

        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }


        if (dto.getStateAction() != null) {
            EventState newState = EventMapper.getNewStateFromUserAction(dto.getStateAction(), event.getState());
            event.setState(newState);
        }

        Event saved = eventRepository.save(event);

        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Long views = 0L; // TODO: получить из сервиса статистики

        return EventMapper.toEventFullDto(saved, confirmedRequests, views);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        // Проверяем что событие принадлежит пользователю
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        // TODO: Реализовать после создания Request entity
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest request) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        // TODO: Реализовать после создания Request entity
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(List.of())
                .rejectedRequests(List.of())
                .build();
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

        // TODO: Запрос к сервису статистики
        return eventIds.stream()
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> 0L
                ));
    }
}
