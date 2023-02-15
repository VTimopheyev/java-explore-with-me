package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;
import ru.practicum.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/admin/users")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createNewUser(@NotNull @RequestBody @Valid UserDto userDto) {
        log.info("Creating new user");
        return userService.createNewUser(userDto);
    }

    @GetMapping
    public Collection<User> getUsers(
            @RequestParam(name = "ids", required = false) List<Long> ids,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("Getting users");
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User deleteUser(@NotNull @PathVariable long userId) {
        log.info("Deleting user ");
        return userService.deleteUser(userId);
    }
}
