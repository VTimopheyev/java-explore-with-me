package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EventEditingNotAllowedException extends RuntimeException {
    public EventEditingNotAllowedException() {
        super("Event can be edited by creator only");
    }
}