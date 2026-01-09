package ru.yandex.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import ru.yandex.practicum.ewm.dto.CategoryDto;
import ru.yandex.practicum.ewm.dto.NewCategoryDto;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;


@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ConflictException(
                    "Категория с именем '" + dto.getName() + "' уже существует"
            );
        }

        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto dto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Категория с именем =" + catId + " не найден")
                );

        if (categoryRepository.existsByNameAndIdNot(dto.getName(), catId)) {
            throw new ConflictException(
                    "Категория с именем '" + dto.getName() + "' уже существует"
            );
        }

        category.setName(dto.getName());
        Category updated = categoryRepository.save(category);
        return CategoryMapper.toDto(updated);
    }

    @Override
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Категория с именем" + catId + " не найден");
        }

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Данная категория не пустая");
        }

        categoryRepository.deleteById(catId);
    }


}
