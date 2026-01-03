package ru.yandex.practicum.ewm.categories.mapper;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.model.Category;


public class CategoryMapper {
    public static Category toEntity(CategoryDto dto) {
        if (dto == null) return null;
        return new Category(dto.getName());
    }

    public static CategoryDto toDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(category.getId(), category.getName());
    }
}
