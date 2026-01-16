package ru.yandex.practicum.ewm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.dto.NewCommentDto;
import ru.yandex.practicum.ewm.service.PrivateCommentService;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class PrivateCommentController {
    private final PrivateCommentService commentService;


    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody NewCommentDto dto) {
        return commentService.createComment(eventId, userId, dto);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId,
            @Valid @RequestBody NewCommentDto dto) {

        return commentService.updateComment(commentId, userId, dto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId) {
        commentService.deleteComment(commentId, userId);
    }

    @PostMapping("/users/{userId}/comments/{commentId}/rate")
    public CommentDto rateComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId,
            @RequestParam boolean helpful) {
        return commentService.rateComment(userId, commentId, helpful);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}/rate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRating(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId) {

        commentService.removeRating(userId, commentId);
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getUserComments(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {

        return commentService.getUserComments(userId, from, size);
    }
}
