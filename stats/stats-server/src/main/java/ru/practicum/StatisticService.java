package ru.practicum;

import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsRecordDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatisticService {
    StatsDto logRequestToStatistics(StatsRecordDto statsDto);

    Collection<StatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
