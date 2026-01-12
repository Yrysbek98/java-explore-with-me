package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.dto.NewCommentDto;

import java.util.List;

public class PrivateCommentServiceImpl implements PrivateCommentService{
    @Override
    public CommentDto createComment(Long eventId, Long userId, NewCommentDto dto) {
        return null;
    }

    @Override
    public CommentDto updateComment(Long commentId, Long userId, NewCommentDto dto) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {

    }

    @Override
    public CommentDto rateComment(Long userId, Long commentId, boolean helpful) {
        return null;
    }

    @Override
    public void removeRating(Long userId, Long commentId) {

    }

    @Override
    public List<CommentDto> getUserComments(Long userId) {
        return List.of();
    }
}
