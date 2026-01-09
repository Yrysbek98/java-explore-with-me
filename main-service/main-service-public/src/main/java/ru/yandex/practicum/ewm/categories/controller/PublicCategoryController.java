package ru.yandex.practicum.ewm.categories.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.ewm.categories.service.PublicCategoryService;
import ru.yandex.practicum.ewm.dto.CategoryDto.CategoryDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private final PublicCategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }

    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoryService.getCategories(from, size);
    }
}
