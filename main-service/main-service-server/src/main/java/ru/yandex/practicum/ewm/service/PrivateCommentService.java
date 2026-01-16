package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.dto.NewCommentDto;

import java.util.List;

public interface PrivateCommentService {

    CommentDto createComment(Long eventId, Long userId, NewCommentDto dto);

    CommentDto updateComment(Long commentId, Long userId, NewCommentDto dto);

    void deleteComment(Long commentId, Long userId);

    CommentDto rateComment(Long userId, Long commentId, boolean helpful);

    void removeRating(Long userId, Long commentId);

    List<CommentDto> getUserComments(Long userId, Integer from, Integer size);

}
