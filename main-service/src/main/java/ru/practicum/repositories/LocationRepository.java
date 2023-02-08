package ru.practicum.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Location;

public interface LocationRepository extends PagingAndSortingRepository<Location, Long> {
}
