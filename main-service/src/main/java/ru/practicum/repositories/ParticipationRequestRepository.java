package ru.practicum.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.status.ParticipationRequestStatus;

import java.util.Collection;

public interface ParticipationRequestRepository extends PagingAndSortingRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findByInitiatorEquals(User initiator);

    int countByEventEqualsAndStatusEquals(Event e, ParticipationRequestStatus status);

    Collection<ParticipationRequest> findByEventEquals(Event event);
}
