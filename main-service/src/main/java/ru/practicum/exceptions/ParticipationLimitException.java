package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParticipationLimitException extends RuntimeException {
    public ParticipationLimitException() {
        super("Event invalid status for requested actions");
    }
}