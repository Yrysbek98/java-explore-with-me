package ru.yandex.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "categories")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping("/{id}")
    public CategoryDto getUserById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping()
    public List<CategoryDto> getAllUsers() {
        return categoryService.getAllCategories();
    }
}
