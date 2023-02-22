package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EventInvalidEventDateException extends RuntimeException {
    public EventInvalidEventDateException() {
        super("Event date can`t be so close to now");
    }
}