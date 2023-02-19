package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.CategoryInvalidNameException;
import ru.practicum.exceptions.CategoryNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repositories.CategoryRepository;
import ru.practicum.repositories.EventRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public Category createNewCategory(Category category) {
        checkCategoryExist(category.getName());

        return categoryRepository.save(category);
    }

    public Category updateCategoryByAdmin(long catId, Category cat) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(CategoryNotFoundException::new);

        checkCategoryExist(cat.getName());


        if (!Objects.isNull(cat.getName())) {
            category.setName(cat.getName());
        }

        return categoryRepository.save(category);
    }

    private void checkCategoryExist(String categoryName) {
        Long count = categoryRepository
                .findAll()
                .stream()
                .filter(c -> c.getName().equals(categoryName))
                .count();
        log.info("Count: " + count);


        if (count > 0) {
            throw new CategoryInvalidNameException();
        }
    }

    public Category deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(CategoryNotFoundException::new);

        checkIfThereAreLinkedEventsToCategory(category);

        categoryRepository.delete(category);
        return category;
    }

    private void checkIfThereAreLinkedEventsToCategory(Category category) {
        Long count = eventRepository
                .findAll()
                .stream()
                .filter(e -> e.getCategory().equals(category))
                .count();

        if (count > 0) {
            throw new CategoryInvalidNameException();
        }
    }

    public List<Category> getCategoriesByAnyUser(int from, int size) {
        PageRequest pr = PageRequest.of((from / size), size);
        return categoryRepository.findByIdNot(0L, pr);
    }

    public Category viewParticularCategoryByAnyUser(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(CategoryNotFoundException::new);
    }
}
