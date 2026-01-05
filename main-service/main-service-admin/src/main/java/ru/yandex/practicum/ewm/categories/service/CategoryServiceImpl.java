package ru.yandex.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.categories.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.exception.exceptionType.ValidationException;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.repository.CategoryRepository;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ValidationException("Категория с таким именем уже существует");
        }
        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto dto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Категория не найдена"));
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ValidationException("Категория с таким именем уже существует");
        }
        category.setName(dto.getName());
        Category updated = categoryRepository.save(category);
        return CategoryMapper.toDto(updated);

    }

    @Override
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Категория не найдена");
        }
        categoryRepository.deleteById(catId);
    }


}
