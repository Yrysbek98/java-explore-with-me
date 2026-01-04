package ru.yandex.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private  final CategoryRepository categoryRepository;

    @Override
    public CategoryDto getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return CategoryMapper.toDto(category.get());
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(CategoryMapper ::toDto).toList();
    }
}
