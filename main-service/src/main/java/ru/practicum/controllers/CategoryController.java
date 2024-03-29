package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.Category;
import ru.practicum.service.CategoryServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @PostMapping(path = "/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public Category createNewCategory(@NotNull @RequestBody @Valid Category category) {
        log.info("Creating new category");
        return categoryService.createNewCategory(category);
    }

    @PatchMapping(path = "/admin/categories/{catId}")
    public Category updateCategoryByAdmin(
            @NotNull @RequestBody @Valid Category category,
            @NotNull @PathVariable long catId) {
        log.info("Updating category");
        return categoryService.updateCategoryByAdmin(catId, category);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Category deleteCategory(@NotNull @PathVariable long catId) {
        log.info("Deleting category by admin");
        return categoryService.deleteCategory(catId);
    }

    @GetMapping(path = "/categories")
    public List<Category> getCategoriesByAnyUser(
            @RequestParam(name = "from", required = false, defaultValue = "0") int from,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        log.info("Getting all categories by any user");
        return categoryService.getCategoriesByAnyUser(from, size);
    }

    @GetMapping(path = "/categories/{catId}")
    public Category viewParticularCategoryByAnyUser(@NotNull @PathVariable long catId) {
        log.info("Viewing category by some user");
        return categoryService.viewParticularCategoryByAnyUser(catId);
    }
}
