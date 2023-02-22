package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Stat;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Stat, Long> {
    List<Stat> findAllByStampAfterAndStampBeforeOrderByStamp(Timestamp start, Timestamp end);

    List<Stat> findAllByStampAfterAndStampBeforeAndUriInOrderByStamp(Timestamp start, Timestamp end, List<String> uris);

}
