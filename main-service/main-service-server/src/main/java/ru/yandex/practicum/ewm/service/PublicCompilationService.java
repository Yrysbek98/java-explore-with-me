package ru.yandex.practicum.ewm.service;

import ru.yandex.practicum.ewm.dto.*;

import java.util.List;

public interface PublicCompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);


    CompilationDto getCompilationById(Long compId);
}
