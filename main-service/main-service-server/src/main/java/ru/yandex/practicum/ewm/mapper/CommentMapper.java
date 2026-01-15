package ru.yandex.practicum.ewm.mapper;

import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.dto.NewCommentDto;
import ru.yandex.practicum.ewm.dto.UserShortDto;
import ru.yandex.practicum.ewm.model.Comment;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.User;

public class CommentMapper {

    public static Comment toComment(NewCommentDto dto, Event event, User author) {
        return Comment.builder()
                .text(dto.getText())
                .event(event)
                .author(author)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment, Boolean userRating) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .author(UserShortDto.builder()
                        .id(comment.getAuthor().getId())
                        .name(comment.getAuthor().getName())
                        .build())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn())
                .status(comment.getStatus())
                .helpfulCount(comment.getHelpfulCount())
                .notHelpfulCount(comment.getNotHelpfulCount())
                .ratingScore(comment.getRatingScore())
                .isEdited(comment.getUpdatedOn() != null)
                .canEdit(comment.canBeEdited())
                .userRating(userRating)
                .moderationReason(comment.getModerationReason())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return toCommentDto(comment, null);
    }
}
