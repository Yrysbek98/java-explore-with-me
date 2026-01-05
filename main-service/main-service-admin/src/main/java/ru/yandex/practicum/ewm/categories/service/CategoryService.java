package ru.yandex.practicum.ewm.categories.service;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.model.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto);
    CategoryDto updateCategory(Long catId, CategoryDto dto);
    void deleteCategory(Long catId);

}
