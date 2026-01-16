package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.enums.CommentStatus;
import ru.yandex.practicum.ewm.enums.ModerationStatus;

import java.util.List;

public interface AdminCommentService {

    List<CommentDto> getAllComments(CommentStatus status);

    CommentDto moderateComment(Long commentId, ModerationStatus moderationStatus, String reason);

    void deleteComment(Long commentId);

    CommentDto sendToModeration(Long commentId, String reason);


    CommentDto publishComment(Long commentId);


}
