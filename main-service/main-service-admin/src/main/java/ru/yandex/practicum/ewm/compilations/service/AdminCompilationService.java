package ru.yandex.practicum.ewm.compilations.service;

import ru.yandex.practicum.ewm.dto.CompilationDto;
import ru.yandex.practicum.ewm.dto.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto addCompilation(NewCompilationDto dto);


    void deleteCompilation(Long compId);


    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);
}
