package ru.yandex.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.service.CategoryService;
import ru.yandex.practicum.ewm.model.Category;


@RestController
@RequiredArgsConstructor
public class CategoryController {
        private final CategoryService categoryService;

    @PostMapping()
    public CategoryDto addNewCategory(@PathVariable Category category) {
        return categoryService.addNewCategory(category);
    }

    @PatchMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id, String name ) {
        return categoryService.updateCategory(id, name);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
