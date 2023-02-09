package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.status.EventStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
    Collection<Event> findByInitiatorEquals(User initiator, PageRequest pr);

    Collection<Event> findByIdInAndStateInAndCategoryInAndStartAfterAndEndBefore(List<Long> ids, List<EventStatus> status, List<Category> categories, LocalDateTime start, LocalDateTime end, PageRequest pr);

    List<Event> findByStateEqualsAndAnnotationContainingOrDescriptionContainingIgnoreCase(EventStatus published, String text, String text1, PageRequest pr);

    List<Event> findByStateEquals(EventStatus published, PageRequest pr);

    List<Event> findByIdIn(List<Long> eventIds);
}
