package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService{

    private  final EventRepository eventRepository;
    private  final UserRepository userRepository;

    @Override
    public List<CommentDto> getEventComments(Long eventId, Long userId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Пользователь с таким id=" + userId + " не найден");
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id=" + userId + " не найден");
        }

        return List.of();
    }

    @Override
    public CommentDto getCommentById(Long commentId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id=" + userId + " не найден");
        }

        return null;
    }
}
