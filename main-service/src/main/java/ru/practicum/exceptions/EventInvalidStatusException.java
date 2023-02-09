package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EventInvalidStatusException extends RuntimeException {
    public EventInvalidStatusException() {
        super("Event invalid status for requested actions");
    }
}