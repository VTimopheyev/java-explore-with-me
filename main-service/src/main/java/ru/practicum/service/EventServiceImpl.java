package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticsClient;
import ru.practicum.dto.*;
import ru.practicum.exceptions.*;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.ParticipationRequestMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.model.*;
import ru.practicum.repositories.*;
import ru.practicum.status.EventStatus;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.status.EventStatus.PENDING;
import static ru.practicum.status.EventStatus.PUBLISHED;
import static ru.practicum.status.ParticipationRequestStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatisticsClient statisticsClient;
    private final ParticipationRequestMapper participationRequestMapper;

    public EventFullDto createNewEvent(EventDto eventDto, long userId) {
        validateEvent(eventDto);

        Float lon = eventDto.getLocation().getLon();
        Float lat = eventDto.getLocation().getLat();
        Location loc = locationRepository.saveAndFlush(new Location(null, lon, lat));

        Event event = new Event();
        event.setCreatedOn(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        event.setAnnotation(eventDto.getAnnotation());

        event.setCategory(categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(CategoryNotFoundException::new));

        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new));

        event.setEventDate(eventDto.getEventDate().toLocalDateTime());

        event.setDescription(eventDto.getDescription());

        event.setLocation(loc);

        event.setPaid(eventDto.isPaid());

        event.setTitle(eventDto.getTitle());

        event.setRequestModeration(eventDto.isRequestModeration());

        if (!Objects.isNull(eventDto.getParticipantLimit())) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        event.setPublishedOn(null);
        event.setState(PENDING);

        return eventMapper.toFullDto(eventRepository.saveAndFlush(event),
                0,
                userMapper.toUserDto(event.getInitiator()),
                0);
    }

    public List<EventFullDto> getEventsOfInitiator(long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        PageRequest pr = PageRequest.of((from / size), size);

        return eventRepository
                .findAll(pr)
                .stream()
                .filter(e -> e.getInitiator().equals(user))
                .map(e -> eventMapper.toFullDto(e, getConfirmedRequests(e),
                        userMapper.toUserDto(user),
                        getViewsOFEvent(e)))
                .sorted(Comparator.comparingInt(EventFullDto::getViews)
                        .reversed())
                .collect(Collectors.toList());
    }

    int getConfirmedRequests(Event e) {
        List<ParticipationRequest> list = participationRequestRepository
                .findAll()
                .stream()
                .filter(p -> p.getEvent().getId().equals(e.getId()) && p.getStatus().equals(CONFIRMED))
                .collect(Collectors.toList());
        return list.size();
    }

    public int getViewsOFEvent(Event e) {
        Timestamp ts = Timestamp.valueOf(e.getCreatedOn());
        Timestamp ts1 = Timestamp.valueOf(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        List<String> uris = List.of("event/" + e.getId());
        String path = "http://stats-server:9090/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        Map<String, Object> parameters = Map.of(
                "start", ts,
                "end", ts1,
                "uris", uris,
                "unique", "false"
        );

        return statisticsClient.get(path, parameters);
    }


    public EventFullDto getSingleEventOfInitiator(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return eventMapper.toFullDto(
                event, getConfirmedRequests(event), userMapper.toUserDto(user), getViewsOFEvent(event));
    }

    public Collection<ParticipationRequestDto> getParticipationRequestsForEventOfInitiator(
            long userId, long eventId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        return participationRequestRepository.findAll()
                .stream()
                .filter(p -> p.getEvent().equals(event))
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<EventFullDto> searchEventsByAdmin(List<Long> ids, List<String> states, List<Long> categoryIds,
                                                        Timestamp start, Timestamp end, int from, int size) {

        PageRequest pr = PageRequest.of((from / size), size);
        Collection<Event> events;

        if (!Objects.isNull(ids) && !ids.isEmpty()) {
            events = eventRepository.findAllByIdNot(0L, pr);
        } else {
            events = eventRepository.findByIdIn(ids, pr);
        }

        if (!Objects.isNull(states) && !states.isEmpty()) {
            List<EventStatus> status = states
                    .stream()
                    .map(EventStatus::valueOf)
                    .collect(Collectors.toList());

            events = events
                    .stream()
                    .filter(event -> status.contains(event.getState()))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(categoryIds) && !categoryIds.isEmpty()) {
            events = events
                    .stream()
                    .filter(event -> categoryIds.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(start)) {
            events = events
                    .stream()
                    .filter(event -> event.getEventDate().isAfter(start.toLocalDateTime()))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(end)) {
            events = events
                    .stream()
                    .filter(event -> event.getEventDate().isBefore(end.toLocalDateTime()))
                    .collect(Collectors.toList());
        }

        return events
                .stream()
                .map(e -> eventMapper.toFullDto(e, getConfirmedRequests(e),
                        userMapper.toUserDto(e.getInitiator()),
                        getViewsOFEvent(e)))
                .sorted(Comparator.comparingInt(EventFullDto::getViews)
                        .reversed())
                .collect(Collectors.toList());
    }


    public Collection<EventFullDto> searchEventsByAnyUser(
            String text, List<Long> categoryIds, boolean paid, Timestamp start, Timestamp end,
            boolean onlyAvailable, String sort, int from, int size) {

        Collection<Event> foundEvents = new ArrayList<>();
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        PageRequest pr = PageRequest.of((from / size), size);

        foundEvents = eventRepository.findAllByStateEquals(PUBLISHED, pr);


        if (!Objects.isNull(text)) {
            foundEvents = foundEvents
                    .stream()
                    .filter(e -> text.toLowerCase().contains(e.getAnnotation().toLowerCase()) ||
                            text.toLowerCase().contains(e.getDescription().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (Objects.isNull(start)) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> event.getEventDate().isAfter(now))
                    .collect(Collectors.toList());
        } else {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> event.getEventDate().isAfter(start.toLocalDateTime()))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(end)) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> event.getEventDate().isBefore(end.toLocalDateTime()))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(categoryIds) && !categoryIds.isEmpty()) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> categoryIds.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(paid)) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> event.isPaid() == paid)
                    .collect(Collectors.toList());
        }

        if (onlyAvailable) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> event.getParticipantLimit() >
                            getConfirmedRequests(event))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(sort)) {
            if (sort.equals("EVENT_DATE")) {
                foundEvents = foundEvents
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            } else if (sort.equals("VIEWS")) {
                foundEvents = foundEvents
                        .stream()
                        .sorted(Comparator.comparingInt(this::getViewsOFEvent))
                        .collect(Collectors.toList());
            }
        }

        return foundEvents
                .stream()
                .map(e -> eventMapper.toFullDto(e, getConfirmedRequests(e),
                        userMapper.toUserDto(e.getInitiator()),
                        getViewsOFEvent(e)))
                .collect(Collectors.toList());
    }

    public EventFullDto viewParticularEventByAnyUser(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (!event.getState().equals(PUBLISHED)) {
            throw new EventNotFoundException();
        }

        return eventMapper.toFullDto(
                event,
                getConfirmedRequests(event),
                userMapper.toUserDto(userRepository.findById(event.getId()).get()),
                getViewsOFEvent(event));
    }


    public EventFullDto updateEventByInitiator(long userId, long eventId, EventDto eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        User initiator = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!initiator.getId().equals(userId)) {
            throw new EventEditingNotAllowedException();
        }

        if (!event.getState().equals(PENDING) && !event.getState().equals(EventStatus.REJECTED)) {
            throw new EventInvalidStatusException();
        }

        if (!Objects.isNull(eventDto.getStateAction())) {
            if (eventDto.getStateAction().equals("CANCEL_REVIEW")) {
                event.setState(EventStatus.CANCELED);
            } else if (eventDto.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState(PENDING);
            }
        }

        if (!Objects.isNull(eventDto.getEventDate())) {
            if (eventDto.getEventDate().toLocalDateTime().isBefore(
                    LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusHours(2L))) {
                throw new EventInvalidEventDateException();
            } else {
                event.setEventDate(eventDto.getEventDate().toLocalDateTime());
            }
        }

        if (!Objects.isNull(eventDto.getAnnotation())) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (!Objects.isNull(eventDto.getCategory())) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(CategoryNotFoundException::new));
        }

        if (!Objects.isNull(eventDto.getDescription())) {
            event.setDescription(eventDto.getDescription());
        }

        if (!Objects.isNull(eventDto.getLocation())) {
            Location location = new Location();
            location.setLon(eventDto.getLocation().getLon());
            location.setLat(eventDto.getLocation().getLat());
            event.setLocation(locationRepository.save(location));
        }

        if (!Objects.isNull(eventDto.getTitle())) {
            event.setTitle(eventDto.getTitle());
        }

        if (eventDto.getParticipantLimit() > event.getParticipantLimit()) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        return eventMapper.toFullDto(eventRepository.saveAndFlush(event),
                getConfirmedRequests(event),
                userMapper.toUserDto(initiator),
                getViewsOFEvent(event));
    }


    public EventStatusResponseDto updateEventParticipationRequestsStatusByInitiator(
            long userId, long eventId, EventStatusDto eventStatusDto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);


        User initiator = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if ((event.getParticipantLimit() == getConfirmedRequests(event)) && (event.getParticipantLimit() > 0)) {
            throw new ParticipationLimitException();
        }

        List<Long> requestsIds = eventStatusDto.getRequestIds();

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        List<ParticipationRequest> parReqs = participationRequestRepository.findByIdIn(requestsIds);

        for (ParticipationRequest pr : parReqs) {

            if ((getConfirmedRequests(event) < event.getParticipantLimit() || event.getParticipantLimit() == 0)
                    && eventStatusDto.getStatus().equals(CONFIRMED)) {
                pr.setStatus(CONFIRMED);
                confirmedRequests.add(participationRequestMapper.toDto(participationRequestRepository.save(pr)));
            } else {
                pr.setStatus(REJECTED);
                rejectedRequests.add(participationRequestMapper.toDto(participationRequestRepository.save(pr)));
            }
        }
        return new EventStatusResponseDto(confirmedRequests, rejectedRequests);
    }


    public EventFullDto updateEventByAdmin(Long eventId, EventDto eventDto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(UserNotFoundException::new);

        if (event.getState().equals(EventStatus.CANCELED)) {
            throw new EventInvalidStatusException();
        }

        if (!Objects.isNull(eventDto.getEventDate()) && eventDto.getEventDate().toLocalDateTime().isBefore(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusHours(1L))) {
            throw new EventInvalidEventDateException();
        }

        if (eventDto.getStateAction().equals("PUBLISH_EVENT") && !event.getState().equals(PENDING)) {
            throw new EventInvalidStatusException();
        } else if (eventDto.getStateAction().equals("PUBLISH_EVENT")) {
            event.setState(PUBLISHED);
            event.setPublishedOn(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        }

        if (eventDto.getStateAction().equals("REJECT_EVENT") && !event.getState().equals(PENDING)) {
            throw new EventInvalidStatusException();
        } else if (eventDto.getStateAction().equals("REJECT_EVENT")) {
            event.setState(EventStatus.REJECTED);
        }

        if (!Objects.isNull(eventDto.getAnnotation())) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (!Objects.isNull(eventDto.getCategory())) {
            event.setCategory(categoryRepository.findById(eventDto.getCategory()).get());
        }
        if (!Objects.isNull(eventDto.getEventDate())) {
            event.setEventDate(eventDto.getEventDate().toLocalDateTime());
        }

        if (!Objects.isNull(eventDto.getDescription())) {
            event.setDescription(eventDto.getDescription());
        }

        if (!Objects.isNull(eventDto.getLocation())) {
            Location location = new Location();
            location.setLon(eventDto.getLocation().getLon());
            location.setLat(eventDto.getLocation().getLat());
            event.setLocation(locationRepository.save(location));
        }

        if (!Objects.isNull(eventDto.getTitle())) {
            event.setTitle(eventDto.getTitle());
        }

        if (eventDto.getParticipantLimit() > 0) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        Event savedEvent = eventRepository.saveAndFlush(event);

        int confReq = getConfirmedRequests(savedEvent);
        UserDto initiator = userMapper.toUserDto(savedEvent.getInitiator());
        int views = getViewsOFEvent(savedEvent);

        return eventMapper.toFullDto(savedEvent, confReq, initiator, views);
    }

    private void validateEvent(EventDto event) {
        if (Objects.isNull(event.getAnnotation()) || event.getAnnotation().isEmpty()) {
            throw new EventValidationException();
        }

        if (Objects.isNull(event.getDescription()) || event.getDescription().isEmpty()) {
            throw new EventValidationException();
        }

        if (Objects.isNull(event.getEventDate())
                || event.getEventDate().toLocalDateTime().isBefore(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))) {
            throw new EventInvalidEventDateException();
        }
    }
}
