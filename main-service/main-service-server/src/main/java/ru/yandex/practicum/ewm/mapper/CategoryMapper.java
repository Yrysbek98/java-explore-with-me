package ru.yandex.practicum.ewm.mapper;

import ru.yandex.practicum.ewm.dto.CategoryDto.CategoryDto;
import ru.yandex.practicum.ewm.dto.CategoryDto.NewCategoryDto;
import ru.yandex.practicum.ewm.model.Category;

public class CategoryMapper {
    public static Category toEntity(NewCategoryDto dto) {
        if (dto == null) return null;
        return new Category(dto.getName());
    }

    public static CategoryDto toDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(category.getId(), category.getName());
    }
}
