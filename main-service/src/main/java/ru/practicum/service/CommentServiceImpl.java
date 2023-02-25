package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.CommentNotFoundException;
import ru.practicum.exceptions.CommentValidationException;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.repositories.CommentRepository;
import ru.practicum.repositories.EventRepository;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;


    public Comment createNewComment(Comment comment, @NotNull long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (comment.getText().isEmpty() || comment.getAuthorName().isEmpty()) {
            throw new CommentValidationException();
        }

        Comment commentToSave = new Comment();
        commentToSave.setAuthorName(comment.getAuthorName());
        commentToSave.setText(comment.getText());
        commentToSave.setCreated(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        commentToSave.setEvent(event);

        return commentRepository.save(commentToSave);
    }

    public List<Comment> getCommentsForEvent(int from, int size, long eventId) {
        PageRequest pr = PageRequest.of((from / size), size);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return commentRepository
                .findAll(pr)
                .stream()
                .filter(c -> c.getEvent().getId().equals(event.getId()))
                .collect(Collectors.toList());
    }

    public Comment deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        commentRepository.delete(comment);
        return comment;
    }
}
