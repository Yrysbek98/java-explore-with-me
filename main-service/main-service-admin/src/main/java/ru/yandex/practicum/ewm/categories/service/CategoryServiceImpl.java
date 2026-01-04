package ru.yandex.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.categories.mapper.CategoryMapper;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {
        Category category = CategoryMapper.toEntity(dto);
        try {
            Category saved = categoryRepository.save(category);
            return CategoryMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Category name already exists: " + dto.getName());
        }
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto dto) {
        Optional<Category> category = categoryRepository.findById(catId);
      //  category.setName(dto.getName());


       // Category updated = categoryRepository.save(category);
        return CategoryMapper.toDto(updated);

    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.deleteById(catId);

    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        Optional<Category> category = categoryRepository.findById(catId);

        return CategoryMapper.toDto(category.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable)
                .map(CategoryMapper::toDto)
                .getContent();
    }

}
