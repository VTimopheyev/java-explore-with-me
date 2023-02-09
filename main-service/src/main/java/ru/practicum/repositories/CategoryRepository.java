package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.model.Category;

import java.util.Collection;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {
    Collection<Category> findAllOrderById(PageRequest pr);
}