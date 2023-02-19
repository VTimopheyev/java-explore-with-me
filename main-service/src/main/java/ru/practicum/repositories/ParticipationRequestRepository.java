package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;

import java.util.Collection;
import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findAllByIdNot(long l);

    List<ParticipationRequest> findByIdIn(List<Long> requestsIds);
}
