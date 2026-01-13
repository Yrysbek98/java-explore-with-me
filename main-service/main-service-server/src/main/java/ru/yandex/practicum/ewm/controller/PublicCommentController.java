package ru.yandex.practicum.ewm.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.service.PublicCommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicCommentController {

    private final PublicCommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventComments(
            @PathVariable @Positive Long eventId,
            @RequestParam(required = false) Long userId) {
        return commentService.getEventComments(eventId, userId);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(
            @PathVariable @Positive Long commentId,
            @RequestParam(required = false) Long userId) {
        return commentService.getCommentById(commentId, userId);
    }
}
