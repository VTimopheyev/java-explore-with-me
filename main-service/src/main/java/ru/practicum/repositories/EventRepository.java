package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.status.EventStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStateEqualsAndAnnotationContainingOrDescriptionContainingIgnoreCase(EventStatus published, String text, String text1, PageRequest pr);

    List<Event> findByStateEquals(EventStatus published, PageRequest pr);
    Collection<Event> findAllByIdNot(long i, PageRequest pr);

    Collection<Event> findByIdInAndStateInAndEventDateAfterAndEventDateBefore(List<Long> ids, List<EventStatus> status, LocalDateTime start, LocalDateTime end, PageRequest pr);

    Collection<Event> findAllByIdIn(List<Long> eventIds);

    Collection<Event> findByIdIn(List<Long> ids, PageRequest pr);
}
