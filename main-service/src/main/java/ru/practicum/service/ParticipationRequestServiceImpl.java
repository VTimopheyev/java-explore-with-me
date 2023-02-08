package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.ParticipationRequestNotFoundException;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.mappers.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.ParticipationRequestRepository;
import ru.practicum.repositories.UserRepository;
import ru.practicum.status.ParticipationRequestStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper mapper;

    public ParticipationRequestDto createNewRequest(long userId, long eventId) {

        ParticipationRequest partReq = new ParticipationRequest();
        partReq.setCreated(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        partReq.setRequester(user);
        partReq.setEvent(event);
        partReq.setStatus(ParticipationRequestStatus.PENDING);

        return mapper.toDto(participationRequestRepository.save(partReq));
    }

    public ParticipationRequestDto cancelParticipationRequest(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ParticipationRequest partReq = participationRequestRepository.findById(requestId)
                .orElseThrow(ParticipationRequestNotFoundException::new);

        partReq.setStatus(ParticipationRequestStatus.CANCELLED);

        return mapper.toDto(participationRequestRepository.save(partReq));
    }

    public Collection<ParticipationRequestDto> getParticipationRequests(long userId) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return participationRequestRepository
                .findByInitiatorEquals(initiator)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
