package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserNameAlreadyExistsException extends RuntimeException {
    public UserNameAlreadyExistsException() {
        super("This username already exists");
    }
}