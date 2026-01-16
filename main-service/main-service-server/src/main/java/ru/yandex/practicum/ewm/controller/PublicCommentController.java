package ru.yandex.practicum.ewm.controller;

import jakarta.validation.constraints.Min;
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
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return commentService.getEventComments(eventId, userId, from, size);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(
            @PathVariable @Positive Long commentId,
            @RequestParam(required = false) Long userId) {
        return commentService.getCommentById(commentId, userId);
    }
}
