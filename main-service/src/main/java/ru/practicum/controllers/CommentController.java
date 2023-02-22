package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.Comment;
import ru.practicum.service.CommentServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentController {

    private final CommentServiceImpl commentService;

    @PostMapping(path = "/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createNewComment(@NotNull @RequestBody @Valid Comment comment,
                                    @PathVariable @NotNull long eventId) {
        log.info("Creating new comment");
        return commentService.createNewComment(comment, eventId);
    }

    @GetMapping(path = "/events/{eventId}/comments")
    public List<Comment> getCommentsForEvent(
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @PathVariable @NotNull long eventId) {
        log.info("Getting all categories by any user");
        return commentService.getCommentsForEvent(from, size, eventId);
    }

    @DeleteMapping("comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Comment deleteCategory(@NotNull @PathVariable long commentId) {
        log.info("Deleting category by admin");
        return commentService.deleteComment(commentId);
    }
}
