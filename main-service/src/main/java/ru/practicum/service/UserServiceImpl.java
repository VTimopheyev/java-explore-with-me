package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserDto;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    public UserDto createNewUser(UserDto userDto) {
        return userDto;
    }

    public Collection<UserDto> getUsers(List<Integer> ids, int from, int size) {
    }

    public UserDto deleteUser(int userId) {
    }
}
