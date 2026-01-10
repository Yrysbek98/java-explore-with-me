package ru.yandex.practicum.ewm.service;


import ru.yandex.practicum.ewm.dto.*;

import java.util.List;

public interface PublicCategoryService {

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getCategories(Integer from, Integer size);

}
