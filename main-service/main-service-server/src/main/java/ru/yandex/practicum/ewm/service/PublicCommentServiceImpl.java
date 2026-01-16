package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.enums.CommentStatus;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CommentMapper;
import ru.yandex.practicum.ewm.model.Comment;
import ru.yandex.practicum.ewm.model.CommentRating;
import ru.yandex.practicum.ewm.repository.CommentRatingRepository;
import ru.yandex.practicum.ewm.repository.CommentRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {

    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CommentRatingRepository ratingRepository;

    @Override
    public List<CommentDto> getEventComments(Long eventId, Long requesterId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с таким id=" + eventId + " не найден");
        }

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Comment> commentsPage = commentRepository.findByEventIdAndStatusOrderByRating(
                eventId, CommentStatus.PUBLISHED, pageable);

        return commentsPage.stream()
                .map(comment -> {
                    Boolean userRating = null;
                    if (requesterId != null) {
                        Optional<CommentRating> rating = ratingRepository
                                .findByCommentIdAndUserId(comment.getId(), requesterId);
                        userRating = rating.map(CommentRating::getIsHelpful).orElse(null);
                    }
                    return CommentMapper.toCommentDto(comment, userRating);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId, Long requesterId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Не был найден комментарий с таким id: " + commentId));

        Boolean userRating = null;
        if (requesterId != null) {
            Optional<CommentRating> rating = ratingRepository
                    .findByCommentIdAndUserId(commentId, requesterId);
            userRating = rating.map(CommentRating::getIsHelpful).orElse(null);
        }

        return CommentMapper.toCommentDto(comment, userRating);

    }
}
