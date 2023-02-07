package ru.practicum.service;

import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsRecordDto;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface StatisticService {
    StatsDto logRequestToStatistics(StatsRecordDto statsDto);

    Collection<StatsDto> getStatistics(Timestamp start, Timestamp end, List<String> uris, Boolean unique);
}
