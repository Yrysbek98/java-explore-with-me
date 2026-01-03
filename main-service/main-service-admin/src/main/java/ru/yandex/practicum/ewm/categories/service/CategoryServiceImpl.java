package ru.yandex.practicum.ewm.categories.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.categories.dto.CategoryDto;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Override
    public CategoryDto addNewCategory(String name) {
        return null;
    }

    @Override
    public CategoryDto updateCategory(Long id) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }
}
