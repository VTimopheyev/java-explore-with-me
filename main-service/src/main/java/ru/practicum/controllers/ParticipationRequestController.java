package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createNewRequest(@NotNull @PathVariable long userId,
                                                    @NotNull @RequestParam long eventId) {
        log.info("Creating new participation request");
        System.out.println(userId +" "+eventId);
        return participationRequestService.createNewRequest(userId, eventId);
    }

    @PatchMapping(path = "{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(
            @NotNull @PathVariable long userId,
            @NotNull @PathVariable long requestId
    ) {
        log.info("Canceling of participation request");
        return participationRequestService.cancelParticipationRequest(
                userId, requestId);
    }

    @GetMapping
    public Collection<ParticipationRequestDto> getParticipationRequests(
            @NotNull @PathVariable long userId
    ) {
        log.info("Viewing event by some user");
        return participationRequestService.getParticipationRequests(userId);
    }
}
