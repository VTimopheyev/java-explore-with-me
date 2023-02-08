package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.EventServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {

    private final EventServiceImpl eventService;

    @PostMapping(path = "/users/{userId}/events")
    public EventFullDto createNewEvent(@NotNull @RequestBody @Valid EventDto eventDto,
                                       @NotNull @PathVariable long userId) {
        log.info("Creating new user");
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
                                        @RequestParam(name = "rangeStart", required = false) LocalDateTime start,
                                        @RequestParam(name = "rangeEnd", required = false) LocalDateTime end,
                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Searching for events by admin");
        return eventService.searchEventsByAdmin(ids, states, categoryIds, start, end, from, size);
    }

    @GetMapping(path = "/events")
    public EventDto searchEventsByAnyUser(@RequestParam(name = "text", required = false) String text,
                                          @RequestParam(name = "categories", required = false) List<Integer> categoryIds,
                                          @RequestParam(name = "paid", required = false) boolean paid,
                                          @RequestParam(name = "rangeStart", required = false) Timestamp start,
                                          @RequestParam(name = "rangeEnd", required = false) Timestamp end,
                                          @RequestParam(name = "onlyAvailable", required = false) boolean onlyAvailable,
                                          @RequestParam(name = "sort", required = false) String sort,
                                          @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                          @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Searching for events by any user");
        return eventService.searchEventsByAnyUser(text, categoryIds, paid, start, end,onlyAvailable, sort, from, size);
    }

    @GetMapping(path = "/events/{id}")
    public EventDtoPlus viewParticularEventByAnyUser(
            @NotNull @PathVariable int eventId
    ) {
        log.info("Viewing event by some user");
        return eventService.viewParticularEventByAnyUser(eventId);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}")
    public EventDto updateEventByInitiator(
            @NotNull @RequestBody @Valid EventDto eventDto,
            @NotNull @PathVariable int userId,
            @NotNull @PathVariable int eventId
    ) {
        log.info("Updating event by initiator");
        return eventService.updateEventByInitiator(userId, eventId, eventDto);
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}/requests")
    public EventDto updateEventParticipationRequestsStatusByInitiator(
            @NotNull @RequestBody @Valid EventStatusDto eventStatusDto,
            @NotNull @PathVariable int userId,
            @NotNull @PathVariable int eventId
    ) {
        log.info("Updating event by initiator");
        return eventService.updateEventByupdateEventParticipationRequestsStatusByInitiatorInitiator(
                userId, eventId, eventStatusDto);
    }

    @PatchMapping(path = "/admin/events/{eventId}")
    public EventDto updateEventByAdmin(
            @NotNull @RequestBody EventDto eventDto,
            @NotNull @PathVariable int eventId
    ) {
        log.info("Updating event by admin");
        return eventService.updateEventByAdmin(eventId, eventDto);
    }


}
