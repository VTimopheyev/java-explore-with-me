package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentValidationException extends RuntimeException {

    public CommentValidationException() {

        super("Invalid comment to save");
    }

}