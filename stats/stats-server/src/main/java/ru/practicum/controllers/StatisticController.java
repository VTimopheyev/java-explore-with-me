package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatisticServiceImpl;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsRecordDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatisticController {

    private final StatisticServiceImpl statisticService;

    @PostMapping("/hit")
    public StatsDto saveRequestToStatistics(@NotNull @RequestBody @Valid StatsRecordDto statsDto) {
        log.info("Creating request to statistics");
        return statisticService.logRequestToStatistics(statsDto);
    }

    @GetMapping("/stats")
    public Collection<StatsDto> getStatistics(
            @NotNull @RequestParam(name = "start") Timestamp start,
            @NotNull @RequestParam(name = "end") Timestamp end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique
    ) {
        log.info("Get statistics for period between {} and {}", start, end);
        return statisticService.getStatistics(start, end, uris, unique);
    }

}
