package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.EventCompilation;

import java.util.Collection;

public interface EventCompilationRepository extends PagingAndSortingRepository<EventCompilation, Long> {
    Collection<EventCompilation> findByPinnedEquals(boolean pinned, PageRequest pr);
}
