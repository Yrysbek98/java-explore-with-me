package ru.yandex.practicum.ewm.compilations.service;

import ru.yandex.practicum.ewm.dto.CompilationDto.CompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationDto.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationDto.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto addCompilation(NewCompilationDto dto);


    void deleteCompilation(Long compId);


    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);
}
