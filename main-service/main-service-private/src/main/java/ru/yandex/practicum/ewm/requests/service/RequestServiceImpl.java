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
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        List<Request> requests = requestRepository.findByRequesterId(userId);
        return RequestMapper.toDtoList(requests);
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));


        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));


        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException(
                    "could not execute statement; SQL [n/a]; constraint [uq_request]; " +
                            "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                            "could not execute statement"
            );
        }


        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("The initiator of the event cannot add a request to participate in his event");
        }


        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published yet");
        }


        Integer participantLimit = event.getParticipantLimit();


        if (participantLimit != 0) {
            Long confirmedCount = requestRepository.countConfirmedRequestsByEventId(eventId);

            if (confirmedCount >= participantLimit) {
                throw new ConflictException("The participant limit has been reached");
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
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(RequestStatus.CANCELED);
        Request updated = requestRepository.save(request);

        return RequestMapper.toDto(updated);
    }
}