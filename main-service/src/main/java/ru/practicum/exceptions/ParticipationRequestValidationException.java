package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParticipationRequestValidationException extends RuntimeException {
    public ParticipationRequestValidationException() {
        super("Wrong credentials for created request");
    }
}