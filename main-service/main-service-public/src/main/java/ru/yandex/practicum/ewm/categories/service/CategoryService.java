package ru.yandex.practicum.ewm.categories.service;


import ru.yandex.practicum.ewm.dto.CategoryDto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto getCategoryById(Long catId);
    List<CategoryDto> getCategories(Integer from, Integer size);

}
