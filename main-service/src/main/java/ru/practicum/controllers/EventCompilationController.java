package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventCompilationDisplayDto;
import ru.practicum.dto.EventCompilationDto;
import ru.practicum.model.EventCompilation;
import ru.practicum.service.EventCompilationServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventCompilationController {

    private final EventCompilationServiceImpl eventCompilationService;

    @PostMapping(path = "/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public EventCompilationDisplayDto createNewEventCompilation(
            @NotNull @RequestBody @Valid EventCompilationDto eventCompilationDto) {
        log.info("Creating new event compilation by admin");
        return eventCompilationService.createNewEventCompilation(eventCompilationDto);
    }

    @PatchMapping(path = "/admin/compilations/{compId}")
    public EventCompilationDisplayDto updateEventCompilationByAdmin(
            @NotNull @RequestBody EventCompilationDto eventCompilationDto,
            @NotNull @PathVariable long compId
    ) {
        log.info("Updating event compilation by admin");
        return eventCompilationService.updateEventCompilationByAdmin(
                eventCompilationDto, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public EventCompilation deleteEventCompilation(@NotNull @PathVariable long compId) {
        log.info("Deleting event compilation");
        return eventCompilationService.deleteEventCompilation(compId);
    }

    @GetMapping("/compilations")
    public Collection<EventCompilationDisplayDto> getCompilationsByAnyUser(
            @RequestParam(name = "pinned", required = false) boolean pinned,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("Getting compilations by any user");
        return eventCompilationService.getCompilationsByAnyUser(pinned, from, size);
    }

    @GetMapping(path = "/compilations/{compId}")
    public EventCompilationDisplayDto getParticularCompilationByAnyUser(
            @NotNull @PathVariable long compId
    ) {
        log.info("Viewing event by some user");
        return eventCompilationService.getParticularCompilationByAnyUser(compId);
    }
}
