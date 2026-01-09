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
import ru.yandex.practicum.ewm.enums.RequestStatus;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.EventMapper;
import ru.yandex.practicum.ewm.mapper.RequestMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.Request;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id=" + userId + " не найден");
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
            throw new ConflictException("Должно содержать дату, которая еще не наступила.");
        }

        Event event = EventMapper.toEntity(dto, user, category);
        Event saved = eventRepository.save(event);


        return EventMapper.toEventFullDto(saved, 0L, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с таким id=" + eventId + " не найден"));

        Long confirmedRequests = requestRepository.countConfirmedRequestsByEventId(eventId);
        Long views = 0L; // TODO: получить из сервиса статистики

        return EventMapper.toEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден ");
        }


        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с таким id=" + eventId + " не найден"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Нельзя изменить статус у данного событие");
        }

        Category category = null;
        if (dto.getCategory() != null) {
            category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + dto.getCategory() + " не найден"));
        }

        EventMapper.updateEventFromUserRequest(event, dto, category);

        if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата должна быть как минимум на 2 часа позже");
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

        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("Событие с таким id=" + eventId + " не найден");
        }


        List<Request> requests = requestRepository.findByEventIdAndEventInitiatorId(eventId, userId);

        return RequestMapper.toDtoList(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest request) {


        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));


        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("Confirmation is not required for this event");
        }


        List<Request> requests = requestRepository.findByIdIn(request.getRequestIds());

        if (requests.isEmpty()) {
            throw new NotFoundException("Requests not found");
        }

        boolean allBelongToEvent = requests.stream()
                .allMatch(r -> r.getEvent().getId().equals(eventId));

        if (!allBelongToEvent) {
            throw new ConflictException("Some requests do not belong to this event");
        }


        boolean allPending = requests.stream()
                .allMatch(r -> r.getStatus() == RequestStatus.PENDING);

        if (!allPending) {
            throw new ConflictException("Request must have status PENDING");
        }

        Long confirmedCount = requestRepository.countConfirmedRequestsByEventId(eventId);
        Integer limit = event.getParticipantLimit();

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if ("CONFIRMED".equals(request.getStatus())) {

            for (Request req : requests) {
                if (confirmedCount < limit) {
                    req.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toDto(req));
                    confirmedCount++;
                } else {

                    req.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toDto(req));
                }
            }

            if (confirmedCount >= limit) {
                List<Request> pendingRequests = requestRepository
                        .findByEventIdAndStatus(eventId, RequestStatus.PENDING);

                for (Request pending : pendingRequests) {
                    if (!request.getRequestIds().contains(pending.getId())) {
                        pending.setStatus(RequestStatus.REJECTED);
                        requestRepository.save(pending);
                    }
                }
            }

        } else if ("REJECTED".equals(request.getStatus())) {
            for (Request req : requests) {
                req.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(RequestMapper.toDto(req));
            }
        }

        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
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
