package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.CategoryNotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repositories.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createNewCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategoryByAdmin(long catId, Category cat) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(CategoryNotFoundException::new);

        category.setName(cat.getName());

        return categoryRepository.save(category);
    }

    public Category deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(CategoryNotFoundException::new);

        categoryRepository.delete(category);
        return category;
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
