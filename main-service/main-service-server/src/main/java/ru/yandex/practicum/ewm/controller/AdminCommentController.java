package ru.yandex.practicum.ewm.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.dto.CommentDto;
import ru.yandex.practicum.ewm.enums.CommentStatus;
import ru.yandex.practicum.ewm.enums.ModerationStatus;
import ru.yandex.practicum.ewm.service.AdminCommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping
    public List<CommentDto> getAllComments(
            @RequestParam(required = false) CommentStatus status,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return adminCommentService.getAllComments(status, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto moderateComment(
            @PathVariable @Positive Long commentId,
            @RequestParam ModerationStatus status,
            @RequestParam(required = false) String reason) {
        return adminCommentService.moderateComment(commentId, status, reason);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId) {
        adminCommentService.deleteComment(commentId);
    }

    @PostMapping("/{commentId}/moderate")
    public CommentDto sendToModeration(
            @PathVariable @Positive Long commentId,
            @RequestParam(required = false) String reason) {
        return adminCommentService.sendToModeration(commentId, reason);
    }

    @PostMapping("/{commentId}/publish")
    public CommentDto publishComment(@PathVariable @Positive Long commentId) {
        return adminCommentService.publishComment(commentId);
    }
}
