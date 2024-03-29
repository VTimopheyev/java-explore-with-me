package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatisticsClient;
import ru.practicum.dto.*;
import ru.practicum.service.EventServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {

    private final EventServiceImpl eventService;
    private final StatisticsClient statisticsClient;

    @PostMapping(path = "/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createNewEvent(@NotNull @RequestBody @Valid EventDto eventDto,
                                       @NotNull @PathVariable long userId) {
        log.info("Creating new event: " + eventDto);
        return eventService.createNewEvent(eventDto, userId);
    }

    @GetMapping(path = "/users/{userId}/events")
    public Collection<EventFullDto> getEventsOfInitiator(
            @NotNull @PathVariable long userId,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("Getting events of user");
        return eventService.getEventsOfInitiator(userId, from, size);
    }

    @GetMapping(path = "/users/{userId}/events/{eventId}")
    public EventFullDto getSingleEventOfInitiator(
            @NotNull @PathVariable long userId,
            @NotNull @PathVariable long eventId
    ) {
        log.info("Getting single event of user");
        return eventService.getSingleEventOfInitiator(userId, eventId);
    }


    @GetMapping(path = "/users/{userId}/events/{eventId}/requests")
    public Collection<ParticipationRequestDto> getParticipationRequestsForEventOfInitiator(
            @NotNull @PathVariable long userId,
            @NotNull @PathVariable long eventId
    ) {
        log.info("Getting participation requests for event of user");
        return eventService.getParticipationRequestsForEventOfInitiator(userId, eventId);
    }

    @GetMapping(path = "/admin/events")
    public Collection<EventFullDto> searchEventsByAdmin(@RequestParam(name = "users", required = false) List<Long> ids,
                                                        @RequestParam(name = "states", required = false) List<String> states,
                                                        @RequestParam(name = "categories", required = false) List<Long> categoryIds,
                                                        @RequestParam(name = "rangeStart", required = false) Timestamp start,
                                                        @RequestParam(name = "rangeEnd", required = false) Timestamp end,
                                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Searching for events by admin");
        return eventService.searchEventsByAdmin(ids, states, categoryIds, start, end, from, size);
    }

    @GetMapping(path = "/events")
    public Collection<EventFullDto> searchEventsByAnyUser(@RequestParam(name = "text", required = false) String text,
                                                          @RequestParam(name = "categories", required = false) List<Long> categoryIds,
                                                          @RequestParam(name = "paid", required = false) boolean paid,
                                                          @RequestParam(name = "rangeStart", required = false) Timestamp start,
                                                          @RequestParam(name = "rangeEnd", required = false) Timestamp end,
                                                          @RequestParam(name = "onlyAvailable", required = false) boolean onlyAvailable,
                                                          @RequestParam(name = "sort", required = false) String sort,
                                                          @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                          HttpServletRequest request) {
        statisticsClient.post(new StatsRecordDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                Timestamp.from(Instant.now())));

        return eventService.searchEventsByAnyUser(text, categoryIds, paid, start, end, onlyAvailable, sort, from, size);
    }

    @GetMapping(path = "/events/{id}")
    public EventFullDto viewParticularEventByAnyUser(
            @NotNull @PathVariable long id,
            HttpServletRequest request
    ) {
        statisticsClient.post(new StatsRecordDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                Timestamp.from(Instant.now())));

        return eventService.viewParticularEventByAnyUser(id);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByInitiator(
            @NotNull @RequestBody @Valid EventDto eventDto,
            @PathVariable @NotNull long userId,
            @PathVariable @NotNull long eventId
    ) {
        log.info("Updating event by initiator");
        return eventService.updateEventByInitiator(userId, eventId, eventDto);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests")
    public EventStatusResponseDto updateEventParticipationsRequestsStatusByInitiator(
            @NotNull @RequestBody @Valid EventStatusDto eventStatusDto,
            @NotNull @PathVariable long userId,
            @NotNull @PathVariable long eventId
    ) {
        log.info("Updating event by initiator");
        return eventService.updateEventParticipationRequestsStatusByInitiator(
                userId, eventId, eventStatusDto);
    }

    @PatchMapping(path = "/admin/events/{eventId}")
    public EventFullDto updateEventByAdmin(
            @RequestBody EventDto eventDto,
            @PathVariable Long eventId
    ) {
        log.info("Updating event by admin");
        return eventService.updateEventByAdmin(eventId, eventDto);
    }


}
