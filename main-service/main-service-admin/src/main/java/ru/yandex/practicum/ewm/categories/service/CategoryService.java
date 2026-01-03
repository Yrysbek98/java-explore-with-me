package ru.yandex.practicum.ewm.categories.service;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;

public interface CategoryService {

    CategoryDto addNewCategory(String name);

    CategoryDto updateCategory(Long id);

    void deleteCategory(Long id);

}
