package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EventValidationException extends RuntimeException {
    public EventValidationException() {
        super("Event credentials invalid: there can be empty description, annotation, wrong or empty event date");
    }
}