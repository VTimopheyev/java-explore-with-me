package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EventCompilation;

import java.util.Collection;
@Repository
public interface EventCompilationRepository extends JpaRepository<EventCompilation, Long> {
    Collection<EventCompilation> findByPinnedEquals(boolean pinned, PageRequest pr);
}
