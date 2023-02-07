package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.ParticipationRequestServiceImpl;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/users/{userId}/requests")
public class ParticipationRequestController {

    private final ParticipationRequestServiceImpl participationRequestService;

    @PostMapping
    public ParticipationRequestDto createNewRequest(@NotNull @PathVariable int userId,
                                                    @NotNull @RequestParam int eventId) {
        log.info("Creating new participation request");
        return participationRequestService.createNewRequest(userId, eventId);
    }

    @PatchMapping(path = "{requestId}/cancel")
    public EventDto cancelParticipationRequest(
            @NotNull @PathVariable int userId,
            @NotNull @PathVariable int requestId
    ) {
        log.info("Canceling of participation request");
        return participationRequestService.cancelParticipationRequest(
                userId, requestId);
    }

    @GetMapping
    public Collection<ParticipationRequestDto> getParticipationRequests(
            @NotNull @PathVariable int userId
    ) {
        log.info("Viewing event by some user");
        return participationRequestService.getParticipationRequests(userId);
    }
}
