package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Collection<User> findAllByIdIn(List<Long> ids, PageRequest pr);
}
