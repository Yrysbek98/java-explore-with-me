package ru.yandex.practicum.ewm.categories.service;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto getCategoryById(Long id);

    List<CategoryDto> getAllCategories();
}
