package ru.yandex.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.model.Request;
import ru.yandex.practicum.ewm.repository.RequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.requests.dto.RequestDto;
import ru.yandex.practicum.ewm.requests.mapper.RequestMapper;

import java.util.List;
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService{

    private  final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public List<RequestDto> getUsersRequests(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return requests
                .stream()
                .map(RequestMapper ::toRequestDto)
                .toList();
    }

    @Override
    public RequestDto addNewRequest(Long userId, Long eventId) {
        return null;
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        return null;
    }
}
