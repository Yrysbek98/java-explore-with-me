package ru.yandex.practicum.ewm.categories.mapper;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.model.Category;


public class CategoryMapper {
    public static Category toEntity(NewCategoryDto dto) {
        if (dto == null) return null;
        return new Category(dto.getName());
    }

    public static Category toEntity(CategoryDto dto) {
        if (dto == null) return null;
        Category category = new Category(dto.getName());
        category.setId(dto.getId());
        return category;
    }

    public static CategoryDto toDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(category.getId(), category.getName());
    }
}
