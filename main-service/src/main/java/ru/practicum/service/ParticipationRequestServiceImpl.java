package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exceptions.*;
import ru.practicum.mappers.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.ParticipationRequestRepository;
import ru.practicum.repositories.UserRepository;
import ru.practicum.status.EventStatus;
import ru.practicum.status.ParticipationRequestStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper mapper;
    private final EventServiceImpl eventService;

    public ParticipationRequestDto createNewRequest(long userId, long eventId) {

        ParticipationRequest partReq = new ParticipationRequest();
        partReq.setCreated(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        checkIfRequestWasAlreadyCreated(event, user);

        if (Objects.equals(user.getId(), event.getInitiator().getId())) {
            throw new ParticipationRequestValidationException();
        }

        if (event.getState().equals(EventStatus.PENDING)) {
            throw new EventInvalidStatusException();
        }

        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() == eventService.getConfirmedRequests(event)) {
            throw new ParticipationLimitException();
        }

        partReq.setRequester(user);
        partReq.setEvent(event);

        if (event.isRequestModeration()) {
            partReq.setStatus(ParticipationRequestStatus.PENDING);
        } else {
            partReq.setStatus(ParticipationRequestStatus.CONFIRMED);
        }


        return mapper.toDto(participationRequestRepository.saveAndFlush(partReq));
    }

    private void checkIfRequestWasAlreadyCreated(Event event, User requester) {
        Long count = participationRequestRepository
                .findAll()
                .stream()
                .filter(p -> p.getRequester().equals(requester) && p.getEvent().equals(event))
                .count();
        if (count > 0) {
            throw new RequestAlreadyCreatedException();
        }
    }

    public ParticipationRequestDto cancelParticipationRequest(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<ParticipationRequest> list = participationRequestRepository
                .findAll()
                .stream()
                .filter(p -> p.getId().equals(requestId))
                .collect(Collectors.toList());

        ParticipationRequest partReq = list.get(0);

        if (!partReq.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            partReq.setStatus(ParticipationRequestStatus.CANCELED);
        } else {
            throw new ParticipationRequestValidationException();
        }

        return mapper.toDto(participationRequestRepository.saveAndFlush(partReq));
    }

    public List<ParticipationRequestDto> getParticipationRequests(long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return participationRequestRepository
                .findAllByIdNot(0L)
                .stream()
                .filter(p -> p.getRequester().equals(requester))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
