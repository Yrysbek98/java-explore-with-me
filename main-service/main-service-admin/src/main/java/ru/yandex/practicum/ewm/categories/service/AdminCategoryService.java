package ru.yandex.practicum.ewm.categories.service;


import ru.yandex.practicum.ewm.dto.*;


public interface AdminCategoryService {

    CategoryDto addCategory(NewCategoryDto dto);

    CategoryDto updateCategory(Long catId, CategoryDto dto);

    void deleteCategory(Long catId);

}
