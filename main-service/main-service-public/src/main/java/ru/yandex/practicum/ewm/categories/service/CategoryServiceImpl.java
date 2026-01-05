package ru.yandex.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
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
    private  final CategoryRepository categoryRepository;

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
