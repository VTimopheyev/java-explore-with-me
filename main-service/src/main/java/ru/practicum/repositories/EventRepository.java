package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Event;
import ru.practicum.status.EventStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    List<Event> findByStateEqualsAndAnnotationContainingOrDescriptionContainingIgnoreCase(EventStatus published, String text, String text1, PageRequest pr);

    List<Event> findByStateEquals(EventStatus published, PageRequest pr);

    List<Event> findByIdIn(List<Long> eventIds);

    Collection<Event> findAllByIdNot(long i, PageRequest pr);

    Collection<Event> findByIdInAndStateInAndEventDateAfterAndEventDateBefore(List<Long> ids, List<EventStatus> status, LocalDateTime start, LocalDateTime end, PageRequest pr);
}
