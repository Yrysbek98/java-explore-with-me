package ru.yandex.practicum.ewm.categories.service;


import ru.yandex.practicum.ewm.dto.CategoryDto.CategoryDto;
import ru.yandex.practicum.ewm.dto.CategoryDto.NewCategoryDto;


public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto);

    CategoryDto updateCategory(Long catId, CategoryDto dto);

    void deleteCategory(Long catId);

}
