package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.Stat;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsMapper {

    public StatsDto toStatsDto(Stat stat) {
        StatsDto dto = new StatsDto();
        dto.setApp(stat.getApp());
        dto.setUri(stat.getUri());
        return dto;
    }
}
