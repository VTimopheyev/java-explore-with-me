package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsRecordDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper;

    @Override
    public StatsDto logRequestToStatistics(StatsRecordDto srDto) {

        Stat stat = new Stat();
        stat.setApp(srDto.getApp());
        stat.setIp(srDto.getIp());
        stat.setUri(srDto.getUri());
        stat.setTimestamp(srDto.getTimestamp());

        return statisticsMapper.toStatsDto(statisticsRepository.save(stat));
    }

    @Override
    public Collection<StatsDto> getStatistics(
            LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (uris.isEmpty() && !unique) {
            return cookStatisticsForAllUrisNotUniqueIp(start, end);
        }

        if (uris.isEmpty() && unique) {
            return cookStatisticsForAllUrisUniqueIp(start, end);
        }

        if (!uris.isEmpty() && !unique) {
            return cookStatisticsForSelectedUrisNotUniqueIp(start, end, uris);
        }

        if (!uris.isEmpty() || unique) {
            return cookStatisticsForSelectedUrisAndUniqueIps(start, end, uris);
        }
        return new ArrayList<>();
    }

    private Collection<StatsDto> cookStatisticsForSelectedUrisAndUniqueIps(
            LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByTimestampAfterAndTimestampBeforeAndUriInOrderByTimestamp(start, end, uris);

        List<List<Stat>> sortedByUri = new ArrayList<>();

        for (String uri : uris) {
            List<Stat> oneUriStat = allStatistics
                    .stream()
                    .filter(s -> s.getUri().equals(uri))
                    .distinct()
                    .collect(Collectors.toList());

            sortedByUri.add(oneUriStat);
        }

        List<StatsDto> statsToSend = new ArrayList<>();

        for (List<Stat> list : sortedByUri) {
            StatsDto dto = new StatsDto();
            dto.setApp(list.get(0).getApp());
            dto.setUri(list.get(0).getUri());
            dto.setHits(list.size());
            statsToSend.add(dto);
        }
        return statsToSend;
    }

    private Collection<StatsDto> cookStatisticsForSelectedUrisNotUniqueIp(
            LocalDateTime start, LocalDateTime end, List<String> uris) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByTimestampAfterAndTimestampBeforeAndUriInOrderByTimestamp(start, end, uris);

        List<List<Stat>> sortedByUri = new ArrayList<>();

        for (String uri : uris) {
            List<Stat> oneUriStat = allStatistics
                    .stream()
                    .filter(s -> s.getUri().equals(uri))
                    .collect(Collectors.toList());

            sortedByUri.add(oneUriStat);
        }

        List<StatsDto> statsToSend = new ArrayList<>();

        for (List<Stat> list : sortedByUri) {
            StatsDto dto = new StatsDto();
            dto.setApp(list.get(0).getApp());
            dto.setUri(list.get(0).getUri());
            dto.setHits(list.size());
            statsToSend.add(dto);
        }

        return statsToSend;
    }

    private Collection<StatsDto> cookStatisticsForAllUrisUniqueIp(
            LocalDateTime start, LocalDateTime end) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByTimestampAfterAndTimestampBeforeOrderByTimestamp(start, end);

        List<String> allUris = getAllUris(allStatistics);

        List<List<Stat>> sortedByUri = new ArrayList<>();

        for (String uri : allUris) {
            List<Stat> oneUriStat = allStatistics
                    .stream()
                    .filter(s -> s.getUri().equals(uri))
                    .distinct()
                    .collect(Collectors.toList());

            sortedByUri.add(oneUriStat);
        }

        List<StatsDto> statsToSend = new ArrayList<>();

        for (List<Stat> list : sortedByUri) {
            StatsDto dto = new StatsDto();
            dto.setApp(list.get(0).getApp());
            dto.setUri(list.get(0).getUri());
            dto.setHits(list.size());
            statsToSend.add(dto);
        }

        return statsToSend;
    }

    private Collection<StatsDto> cookStatisticsForAllUrisNotUniqueIp(
            LocalDateTime start, LocalDateTime end) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByTimestampAfterAndTimestampBeforeOrderByTimestamp(start, end);

        List<String> allUris = getAllUris(allStatistics);

        List<List<Stat>> sortedByUri = new ArrayList<>();

        for (String uri : allUris) {
            List<Stat> oneUriStat = allStatistics
                    .stream()
                    .filter(s -> s.getUri().equals(uri))
                    .collect(Collectors.toList());

            sortedByUri.add(oneUriStat);
        }

        List<StatsDto> statsToSend = new ArrayList<>();

        for (List<Stat> list : sortedByUri) {
            StatsDto dto = new StatsDto();
            dto.setApp(list.get(0).getApp());
            dto.setUri(list.get(0).getUri());
            dto.setHits(list.size());
            statsToSend.add(dto);
        }

        return statsToSend;
    }


    private List<String> getAllUris(List<Stat> allStatistics) {
        List<String> allUris = new ArrayList<>();
        for (Stat s : allStatistics) {
            if (!allUris.contains(s.getUri())) {
                allUris.add(s.getUri());
            }
        }
        return allUris;
    }

}
