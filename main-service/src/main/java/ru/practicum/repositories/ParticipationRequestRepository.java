package ru.practicum.repositories;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.status.ParticipationRequestStatus;

import java.util.Collection;

public interface ParticipationRequestRepository extends PagingAndSortingRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findAllByIdNot(long l);

    Collection<ParticipationRequest> findAll();

    @Query(" select count(p) " +
            "from ParticipationRequest p " +
            "where p.event.id = ?1 and p.status = ?2 ")
    int countRequestsWithStatus(long eventId, ParticipationRequestStatus status);
}
