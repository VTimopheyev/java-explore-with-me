package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Collection<User> findAllByIdIn(List<Long> ids, PageRequest pr);
}
