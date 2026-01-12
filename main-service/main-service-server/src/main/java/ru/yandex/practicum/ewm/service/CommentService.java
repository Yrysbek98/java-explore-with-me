package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.CommentDto;

public interface CommentService {

    CommentDto createComment(Long eventId, Long userId);
}
