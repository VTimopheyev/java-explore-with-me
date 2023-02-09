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
import static ru.practicum.status.ParticipationRequestStatus.CANCELLED;
import static ru.practicum.status.ParticipationRequestStatus.CONFIRMED;

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
        Event event = new Event();
        event.setCreatedOn(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        event.setAnnotation(eventDto.getAnnotation());

        event.setCategory(categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(CategoryNotFoundException::new));

        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new));

        event.setEventDate(eventDto.getEventDate());

        event.setDescription(eventDto.getDescription());

        Location location = new Location();
        location.setLongitude(eventDto.getLocation().getLongitude());
        location.setLatitude(eventDto.getLocation().getLatitude());
        event.setLocation(locationRepository.save(location));

        event.setPaid(eventDto.isPaid());

        event.setTitle(eventDto.getTitle());

        event.setRequestModeration(eventDto.isRequestModeration());

        if (!Objects.isNull(eventDto.getParticipantLimit())) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        event.setPublishedOn(null);
        event.setState(PENDING);

        return eventMapper.toFullDto(eventRepository.save(event),
                0,
                userMapper.toUserDto(event.getInitiator()),
                0);
    }

    public List<EventFullDto> getEventsOfInitiator(long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        PageRequest pr = PageRequest.of((from / size), size);

        return eventRepository
                .findByInitiatorEquals(user, pr)
                .stream()
                .map(e -> eventMapper.toFullDto(e, getConfirmedRequests(e),
                        userMapper.toUserDto(user),
                        getViewsOFEvent(e)))
                .sorted(Comparator.comparingInt(EventFullDto::getViews)
                        .reversed())
                .collect(Collectors.toList());
    }

    int getConfirmedRequests(Event e) {
        return participationRequestRepository.countByEventEqualsAndStatusEquals(e, CONFIRMED);
    }

    int getViewsOFEvent(Event e) {
        Timestamp ts = Timestamp.valueOf(e.getCreatedOn());
        Timestamp ts1 = Timestamp.valueOf(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        String path = "/events/" + e.getId();
        Map<String, Object> parameters = Map.of("start", ts,
                "end", ts1, "unique", "false");

        List<StatsDto> eventStatistics = (List<StatsDto>) statisticsClient.get(path, parameters).getBody();

        if (eventStatistics.isEmpty()) {
            return 0;
        }

        return eventStatistics.get(0).getHits();
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

        return participationRequestRepository.findByEventEquals(event)
                .stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<EventFullDto> searchEventsByAdmin(List<Long> ids, List<String> states, List<Long> categoryIds, LocalDateTime start, LocalDateTime end, int from, int size) {

        List<EventStatus> status = states
                .stream()
                .map(EventStatus::valueOf)
                .collect(Collectors.toList());

        List<Category> categories = categoryIds
                .stream()
                .map(i -> categoryRepository.findById(i).get())
                .collect(Collectors.toList());

        PageRequest pr = PageRequest.of((from / size), size);

        return eventRepository
                .findByIdInAndStateInAndCategoryInAndStartAfterAndEndBefore(ids, status, categories, start, end, pr)
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

        List<Event> foundEvents;
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        PageRequest pr = PageRequest.of((from / size), size);

        if (!Objects.isNull(text)) {
            foundEvents = eventRepository.findByStateEqualsAndAnnotationContainingOrDescriptionContainingIgnoreCase(
                    PUBLISHED, text, text, pr);
        } else {
            foundEvents = eventRepository.findByStateEquals(PUBLISHED, pr);
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

        if (!categoryIds.isEmpty()) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> categoryIds.contains(event.getCategory()))
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(paid)) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> event.isPaid() == paid)
                    .collect(Collectors.toList());
        }

        if (!Objects.isNull(onlyAvailable)) {
            foundEvents = foundEvents
                    .stream()
                    .filter(event -> event.getParticipantLimit() >
                            participationRequestRepository.countByEventEqualsAndStatusEquals(event, CONFIRMED))
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

        if (!event.getState().equals(PENDING) || !event.getState().equals(CANCELLED)) {
            throw new EventInvalidStatusException();
        }

        if (eventDto.getEventDate().isBefore(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusHours(2L))) {
            throw new EventInvalidEventDateException();
        }

        event.setAnnotation(eventDto.getAnnotation());

        event.setCategory(categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(CategoryNotFoundException::new));

        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new));

        event.setEventDate(eventDto.getEventDate());

        event.setDescription(eventDto.getDescription());

        Location location = new Location();
        location.setLongitude(eventDto.getLocation().getLongitude());
        location.setLatitude(eventDto.getLocation().getLatitude());
        event.setLocation(locationRepository.save(location));

        event.setPaid(eventDto.isPaid());

        event.setTitle(eventDto.getTitle());

        event.setRequestModeration(eventDto.isRequestModeration());

        if (!Objects.isNull(eventDto.getParticipantLimit())) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        event.setState(eventDto.getState());

        return eventMapper.toFullDto(eventRepository.save(event),
                getConfirmedRequests(event),
                userMapper.toUserDto(event.getInitiator()),
                getViewsOFEvent(event));
    }


    public EventStatusResponseDto updateEventByUpdateEventParticipationRequestsStatusByInitiatorInitiator(
            long userId, long eventId, EventStatusDto eventStatusDto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);


        if (getConfirmedRequests(event) == eventRepository.findById(eventId).get().getParticipantLimit() ||
                !event.isRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ParticipationLimitException();
        }

        List<Long> requestsIds = eventStatusDto.getIds();

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (long reqId : requestsIds) {
            ParticipationRequest pr = participationRequestRepository.findById(reqId).get();
            if (getConfirmedRequests(event) < event.getParticipantLimit()) {
                pr.setStatus(CONFIRMED);
                confirmedRequests.add(participationRequestMapper.toDto(participationRequestRepository.save(pr)));
            } else {
                pr.setStatus(CANCELLED);
                rejectedRequests.add(participationRequestMapper.toDto(participationRequestRepository.save(pr)));
            }
        }
        return new EventStatusResponseDto(confirmedRequests, rejectedRequests);
    }


    public EventFullDto updateEventByAdmin(long eventId, EventDto eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(EventNotFoundException::new);

        if (eventDto.getEventDate().isBefore(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusHours(1L))) {
            throw new EventInvalidEventDateException();
        }

        if (eventDto.getStateAction().equals("PUBLISH_EVENT") && !event.getState().equals(PENDING)) {
            throw new EventInvalidStatusException();
        }else if(eventDto.getStateAction().equals("PUBLISH_EVENT")){
            event.setState(PUBLISHED);
        }

        if (eventDto.getStateAction().equals("CANCEL_EVENT") && !event.getState().equals(PENDING)) {
            throw new EventInvalidStatusException();
        }else if(eventDto.getStateAction().equals("CANCEL_EVENT")){
            event.setState(EventStatus.CANCELLED);
        }

        event.setAnnotation(eventDto.getAnnotation());

        event.setCategory(categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(CategoryNotFoundException::new));

        event.setEventDate(eventDto.getEventDate());

        event.setDescription(eventDto.getDescription());

        Location location = new Location();
        location.setLongitude(eventDto.getLocation().getLongitude());
        location.setLatitude(eventDto.getLocation().getLatitude());
        event.setLocation(locationRepository.save(location));

        event.setPaid(eventDto.isPaid());

        event.setTitle(eventDto.getTitle());

        event.setRequestModeration(eventDto.isRequestModeration());

        if (!Objects.isNull(eventDto.getParticipantLimit())) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        event.setState(eventDto.getState());

        return eventMapper.toFullDto(eventRepository.save(event),
                getConfirmedRequests(event),
                userMapper.toUserDto(event.getInitiator()),
                getViewsOFEvent(event));
    }


}
