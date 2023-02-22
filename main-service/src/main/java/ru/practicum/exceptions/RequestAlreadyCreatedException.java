package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RequestAlreadyCreatedException extends RuntimeException {
    public RequestAlreadyCreatedException() {
        super("Event invalid status for requested actions");
    }
}
