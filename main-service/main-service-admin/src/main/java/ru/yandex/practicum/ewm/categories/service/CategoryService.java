package ru.yandex.practicum.ewm.categories.service;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.model.Category;

public interface CategoryService {

    CategoryDto addNewCategory(Category category);

    CategoryDto updateCategory(Long id);

    void deleteCategory(Long id);

}
