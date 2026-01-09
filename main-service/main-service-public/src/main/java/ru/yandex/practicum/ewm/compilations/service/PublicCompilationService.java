package ru.yandex.practicum.ewm.compilations.service;

import ru.yandex.practicum.ewm.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);


    CompilationDto getCompilationById(Long compId);
}
