package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.StatisticsClient;
import ru.practicum.dto.*;
import ru.practicum.exceptions.CategoryNotFoundException;
import ru.practicum.exceptions.EventNotFoundException;
import ru.practicum.exceptions.UserNotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.ParticipationRequestMapper;
import ru.practicum.mappers.UserMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repositories.*;
import ru.practicum.status.EventStatus;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.status.EventStatus.PENDING;
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

    private int getConfirmedRequests(Event e) {
        return participationRequestRepository.countByEventEqualsAndStatusEquals(e, CONFIRMED);
    }

    private int getViewsOFEvent(Event e) {
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


}
