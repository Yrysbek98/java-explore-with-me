package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.enums.CommentStatus;
import ru.yandex.practicum.ewm.enums.ModerationStatus;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CommentMapper;
import ru.yandex.practicum.ewm.model.Comment;

import ru.yandex.practicum.ewm.repository.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> getAllComments(CommentStatus status, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "createdOn"));

        Page<Comment> commentsPage;
        if (status != null) {
            commentsPage = commentRepository.findByStatus(status, pageable);
        } else {
            commentsPage = commentRepository.findAll(pageable);
        }

        return commentsPage.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }


    @Override
    public CommentDto moderateComment(Long commentId, ModerationStatus moderationStatus, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий  не найдено с таким  id: " + commentId));

        CommentStatus newStatus = CommentStatus.valueOf(moderationStatus.name());
        comment.setStatus(newStatus);
        if (reason != null && !reason.isBlank()) {
            comment.setModerationReason(reason);
        }

        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public void deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий  не найдено с таким id: " + commentId));

        comment.setStatus(CommentStatus.DELETED);
        commentRepository.save(comment);

    }

    @Override
    public CommentDto sendToModeration(Long commentId, String reason) {


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий  не найдено с таким  id: " + commentId));

        comment.setStatus(CommentStatus.PENDING_REVIEW);
        comment.setModerationReason(reason);

        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto publishComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий  не найдено с таким  id: " + commentId));

        comment.setStatus(CommentStatus.PUBLISHED);
        comment.setModerationReason(null);

        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }
}
