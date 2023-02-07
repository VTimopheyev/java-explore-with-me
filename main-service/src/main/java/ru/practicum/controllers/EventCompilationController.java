package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventCompilationDto;
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
    public EventCompilationDto createNewEventCompilation(
            @NotNull @RequestBody @Valid EventCompilationDto eventCompilationDto) {
        log.info("Creating new event compilation by admin");
        return eventCompilationService.createNewEventCompilation(eventCompilationDto);
    }

    @PatchMapping(path = "/admin/compilations/{compId}")
    public EventCompilationDto updateEventCompilationByAdmin(
            @NotNull @RequestBody EventCompilationDto eventCompilationDto,
            @NotNull @PathVariable int compId
    ) {
        log.info("Updating event compilation by admin");
        return eventCompilationService.updateEventCompilationByAdmin(
                eventCompilationDto, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public EventCompilationDto deleteEventCompilation(@NotNull @PathVariable int compId) {
        log.info("Deleting event compilation");
        return eventCompilationService.deleteEventCompilation(compId);
    }

    @GetMapping("/compilations")
    public Collection<EventCompilationDto> getCompilationsByAnyUser(
            @RequestParam(name = "pinned", required = false) boolean pinned,
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        log.info("Getting compilations by any user");
        return eventCompilationService.getCompilationsByAnyUser(pinned, from, size);
    }

    @GetMapping(path = "/compilations/{compId}")
    public EventCompilationDto getParticularCompilationByAnyUser(
            @NotNull @PathVariable int compId
    ) {
        log.info("Viewing event by some user");
        return eventCompilationService.getParticularCompilationByAnyUser(eventCompId);
    }
}
