package ru.yandex.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.RequestDto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.enums.RequestStatus;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.RequestMapper;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.Request;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;


import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id=" + userId + "не найден");
        }

        List<Request> requests = requestRepository.findByRequesterId(userId);
        return RequestMapper.toDtoList(requests);
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id=" + userId + " не найден"));


        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с таким id=" + eventId + " не найден"));


        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Не удалось выполнить запрос");
        }


        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор мероприятия не может добавить заявку на участие в своем мероприятии.");
        }


        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие еще не опубликовано");
        }


        Integer participantLimit = event.getParticipantLimit();


        if (participantLimit != 0) {
            Long confirmedCount = requestRepository.countConfirmedRequestsByEventId(eventId);

            if (confirmedCount >= participantLimit) {
                throw new ConflictException("Достигнут лимит участников.");
            }
        }


        RequestStatus status;
        if (!event.getRequestModeration() || participantLimit == 0) {

            status = RequestStatus.CONFIRMED;
        } else {
            status = RequestStatus.PENDING;
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .status(status)
                .build();

        Request saved = requestRepository.save(request);
        return RequestMapper.toDto(saved);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Запрос с таким  id=" + requestId + " не найден"));
        request.setStatus(RequestStatus.CANCELED);
        Request updated = requestRepository.save(request);

        return RequestMapper.toDto(updated);
    }
}