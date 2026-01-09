package ru.yandex.practicum.ewm.categories.service;


import ru.yandex.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getCategories(Integer from, Integer size);

}
