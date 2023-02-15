package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.status.ParticipationRequestStatus;

import java.util.Collection;
import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findAllByIdNot(long l);

    @Query(" select count(p) " +
            "from ParticipationRequest p " +
            "where p.event.id = ?1 and p.status = ?2 ")
    int countRequestsWithStatus(long eventId, ParticipationRequestStatus status);

    int countByIdAndStatusEquals(Long id, ParticipationRequestStatus confirmed);

    List<ParticipationRequest> findByIdIn(List<Long> requestsIds);
}
