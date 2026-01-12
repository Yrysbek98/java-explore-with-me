package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.CommentDto;

import java.util.List;

public interface PublicCommentService {

    List<CommentDto> getEventComments(Long eventId, Long userId);

    CommentDto getCommentById(Long commentId, Long userId);
}
