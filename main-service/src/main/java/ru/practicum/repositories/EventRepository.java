package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.status.EventStatus;

import java.util.Collection;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Collection<Event> findAllByIdNot(long i, PageRequest pr);

    Collection<Event> findAllByIdIn(List<Long> eventIds);

    Collection<Event> findByIdIn(List<Long> ids, PageRequest pr);

    Collection<Event> findAllByStateEquals(EventStatus published, PageRequest pr);
}
