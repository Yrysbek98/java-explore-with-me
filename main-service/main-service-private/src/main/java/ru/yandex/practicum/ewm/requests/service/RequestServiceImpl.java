package ru.yandex.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.enums.RequestStatus;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.Request;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.requests.dto.RequestDto;
import ru.yandex.practicum.ewm.requests.mapper.RequestMapper;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestDto> getUsersRequests(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests
                .stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @Override
    public RequestDto addNewRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь не найдено")
                );
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new NotFoundException("Событие не найдено")
                );

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Повторный запрос");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии ");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие еще не опубликовано ");
        }
        ;
        Long count = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.PENDING);
        if (event.getParticipantLimit() <= count) {
            throw new ConflictException("У события достигнут лимит запросов на участие  ");
        }
        RequestStatus status;
        if (event.getRequestModeration() == false) {
            status = RequestStatus.CONFIRMED;
        } else {
            status = RequestStatus.PENDING;
        }
        Request request = new Request(user, event, status);
        Request saved = requestRepository.save(request);
        return RequestMapper.toRequestDto(saved);

    }

    @Override
    public void cancelRequest(Long userId, Long requestId) {
        if (userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        requestRepository.deleteById(requestId);
    }
}
