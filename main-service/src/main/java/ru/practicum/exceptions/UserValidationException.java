package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserValidationException extends RuntimeException {

    public UserValidationException() {

        super("Wrong User credentials: invalid email is tried to be set");
    }

}
