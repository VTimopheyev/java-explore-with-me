package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.StatisticsMapper;
import ru.practicum.repository.StatisticsRepository;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsRecordDto;
import ru.practicum.model.Stat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
        stat.setStamp(srDto.getTimestamp());

        return statisticsMapper.toStatsDto(statisticsRepository.save(stat));
    }

    @Override
    public Collection<StatsDto> getStatistics(
            Timestamp start, Timestamp end, List<String> uris, Boolean unique) {

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
            Timestamp start, Timestamp end, List<String> uris) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByStampAfterAndStampBeforeAndUriInOrderByStamp(start, end, uris);

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
        return statsToSend
                .stream()
                .sorted(Comparator.comparingInt(StatsDto::getHits)
                        .reversed())
                .collect(Collectors.toList());
    }

    private Collection<StatsDto> cookStatisticsForSelectedUrisNotUniqueIp(
            Timestamp start, Timestamp end, List<String> uris) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByStampAfterAndStampBeforeAndUriInOrderByStamp(start, end, uris);

        List<List<Stat>> sortedByUri = new ArrayList<>();

        for (String uri : uris) {
            List<Stat> oneUriStat = allStatistics
                    .stream()
                    .filter(s -> s.getUri().equals(uri))
                    .collect(Collectors.toList());

            if (!oneUriStat.isEmpty()) {
                sortedByUri.add(oneUriStat);
            }
        }

        List<StatsDto> statsToSend = new ArrayList<>();

        for (List<Stat> list : sortedByUri) {
            StatsDto dto = new StatsDto();
            dto.setApp(list.get(0).getApp());
            dto.setUri(list.get(0).getUri());
            dto.setHits(list.size());
            statsToSend.add(dto);
        }

        return statsToSend
                .stream()
                .sorted(Comparator.comparingInt(StatsDto::getHits)
                        .reversed())
                .collect(Collectors.toList());
    }

    private Collection<StatsDto> cookStatisticsForAllUrisUniqueIp(
            Timestamp start, Timestamp end) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByStampAfterAndStampBeforeOrderByStamp(start, end);

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

        return statsToSend
                .stream()
                .sorted(Comparator.comparingInt(StatsDto::getHits)
                        .reversed())
                .collect(Collectors.toList());
    }

    private Collection<StatsDto> cookStatisticsForAllUrisNotUniqueIp(
            Timestamp start, Timestamp end) {
        List<Stat> allStatistics = statisticsRepository
                .findAllByStampAfterAndStampBeforeOrderByStamp(start, end);

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

        return statsToSend
                .stream()
                .sorted(Comparator.comparingInt(StatsDto::getHits)
                        .reversed())
                .collect(Collectors.toList());
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

    /*public Integer getHitsForTheEvent(Timestamp start, Timestamp end, long id, Boolean unique) {
        getStatistics(start, end, List.of("event/" + id), unique);
    }*/
}
