package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserDto;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.exceptions.UserValidationException;
import ru.practicum.model.User;
import ru.practicum.repositories.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public User createNewUser(UserDto userDto) {

        if (!userDto.getEmail().contains("@")) {
            throw new UserValidationException();
        }

        User newUser = new User();

        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());

        return userRepository.save(newUser);
    }

    public Collection<User> getUsers(List<Long> ids, int from, int size) {
        PageRequest pr = PageRequest.of((from / size), size);
        return userRepository.findAllByIdIn(ids, pr);
    }

    public User deleteUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        return user;
    }
}
