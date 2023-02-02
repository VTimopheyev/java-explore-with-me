package ru.practicum;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatisticsRepository extends PagingAndSortingRepository<Stat, Long> {
    List<Stat> findAllByTimestampAfterAndTimestampBeforeAndUriInOrderById(LocalDateTime start, LocalDateTime end, String[] uris);

    List<Stat> findAllByTimestampAfterAndTimestampBeforeOrderById(LocalDateTime start, LocalDateTime end);

    List<Stat> findAllByTimestampAfterAndTimestampBeforeOrderByTimestamp(LocalDateTime start, LocalDateTime end);

    List<Stat> findAllByTimestampAfterAndTimestampBeforeAndUriInOrderByTimestamp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
