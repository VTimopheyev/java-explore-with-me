package ru.practicum.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
