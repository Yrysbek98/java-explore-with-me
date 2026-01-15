package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.dto.NewCommentDto;
import ru.yandex.practicum.ewm.enums.CommentStatus;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.exception.exceptionType.ValidationException;
import ru.yandex.practicum.ewm.mapper.CommentMapper;
import ru.yandex.practicum.ewm.model.Comment;
import ru.yandex.practicum.ewm.model.CommentRating;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CommentRatingRepository;
import ru.yandex.practicum.ewm.repository.CommentRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentRatingRepository ratingRepository;

    private static final int AUTO_DELETE_THRESHOLD = 3;

    @Override
    public CommentDto createComment(Long eventId, Long userId, NewCommentDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено с таким id: " + eventId));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие еще не опубликовано");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с таким id: " + userId));

        Comment comment = CommentMapper.toComment(dto, event, user);
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long commentId, Long userId, NewCommentDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с таким id: " + commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Можно редактировать только свой комментарий");
        }

        if (!comment.canBeEdited()) {
            throw new ValidationException("Комментарий можно редактировать только в течение 1 часа с момента его создания.");
        }

        comment.setText(dto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с таким id: " + commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Можно редактировать только свой комментарий");
        }

        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);

    }

    @Override
    public CommentDto rateComment(Long userId, Long commentId, boolean isHelpful) {


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комменатрий не найден таким id: " + commentId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с таким id: " + userId));

        if (comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Вы не можете оценить свой собственный комментарий.");
        }

        Optional<CommentRating> existingRating = ratingRepository
                .findByCommentIdAndUserId(commentId, userId);


        if (existingRating.isPresent()) {
            CommentRating rating = existingRating.get();

            if (rating.getIsHelpful().equals(isHelpful)) {
                throw new ConflictException("Вы уже оценили этот комментарий таким же образом.");
            }

            if (rating.getIsHelpful()) {
                comment.setHelpfulCount(comment.getHelpfulCount() - 1);
                comment.setNotHelpfulCount(comment.getNotHelpfulCount() + 1);
            } else {
                comment.setHelpfulCount(comment.getHelpfulCount() + 1);
                comment.setNotHelpfulCount(comment.getNotHelpfulCount() - 1);
            }

            rating.setIsHelpful(isHelpful);
            ratingRepository.save(rating);
        } else {
            CommentRating rating = CommentRating.builder()
                    .comment(comment)
                    .user(user)
                    .isHelpful(isHelpful)
                    .build();

            if (isHelpful) {
                comment.setHelpfulCount(comment.getHelpfulCount() + 1);
            } else {
                comment.setNotHelpfulCount(comment.getNotHelpfulCount() + 1);
            }

            ratingRepository.save(rating);
        }
        if (comment.shouldBeAutoDeleted(AUTO_DELETE_THRESHOLD)) {
            comment.setStatus(CommentStatus.DELETED);
        }

        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment, isHelpful);
    }

    @Override
    public void removeRating(Long userId, Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с таким id: " + commentId));

        CommentRating rating = ratingRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));

        if (rating.getIsHelpful()) {
            comment.setHelpfulCount(comment.getHelpfulCount() - 1);
        } else {
            comment.setNotHelpfulCount(comment.getNotHelpfulCount() - 1);
        }

        ratingRepository.delete(rating);
        commentRepository.save(comment);

    }

    @Override
    public List<CommentDto> getUserComments(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден с таким id: " + userId);
        }

        List<Comment> comments = commentRepository.findByAuthorId(
                userId, Sort.by(Sort.Direction.DESC, "createdOn"));

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}

