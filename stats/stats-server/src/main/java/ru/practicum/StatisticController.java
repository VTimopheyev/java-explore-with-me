package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsRecordDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatisticController {

    private final StatisticServiceImpl statisticService;

    @PostMapping("/hit")
    public StatsDto saveRequestToStatistics(@NotNull @RequestBody @Valid StatsRecordDto statsDto) {
        log.info("Creating request to to statistics");
        return statisticService.logRequestToStatistics(statsDto);
    }

    @GetMapping("/stats")
    public Collection<StatsDto>getStatistics(@NotNull @RequestParam(name = "start") LocalDateTime start,
                                    @NotNull @RequestParam(name = "end") LocalDateTime end,
                                    @RequestParam(name = "uris", required = false) List<String> uris,
                                    @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique
                                               ) {
        log.info("Get statistics for period between {} and {}", start, end);
        return statisticService.getStatistics(start, end, uris, unique);
    }

}
