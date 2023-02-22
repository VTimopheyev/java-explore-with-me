package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryInvalidNameException extends RuntimeException {
    public CategoryInvalidNameException() {
        super("Event invalid status for requested actions");
    }
}
