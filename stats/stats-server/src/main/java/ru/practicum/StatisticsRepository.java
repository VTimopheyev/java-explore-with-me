package ru.practicum;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Stat;

import java.sql.Timestamp;
import java.util.List;

public interface StatisticsRepository extends PagingAndSortingRepository<Stat, Long> {
    List<Stat> findAllByStampAfterAndStampBeforeOrderByStamp(Timestamp start, Timestamp end);

    List<Stat> findAllByStampAfterAndStampBeforeAndUriInOrderByStamp(Timestamp start, Timestamp end, List<String> uris);

}
