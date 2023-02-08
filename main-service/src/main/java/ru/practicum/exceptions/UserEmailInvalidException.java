package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailInvalidException extends RuntimeException {
    public UserEmailInvalidException() {

        super("User with provided email already registered");
    }
}


