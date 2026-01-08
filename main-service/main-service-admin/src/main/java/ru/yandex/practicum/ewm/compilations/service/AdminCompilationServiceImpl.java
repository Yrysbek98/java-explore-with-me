package ru.yandex.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.dto.CompilationDto.CompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationDto.NewCompilationDto;
import ru.yandex.practicum.ewm.dto.CompilationDto.UpdateCompilationRequest;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.mapper.CompilationMapper;
import ru.yandex.practicum.ewm.model.Compilation;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CompilationRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.RequestRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;


    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {

        if (compilationRepository.existsByTitle(dto.getTitle())) {
            throw new ConflictException(
                    "could not execute statement; SQL [n/a]; constraint [uq_compilation_name]; " +
                            "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                            "could not execute statement"
            );
        }


        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));

            // Проверяем что все события найдены
            if (events.size() != dto.getEvents().size()) {
                throw new NotFoundException("Some events not found");
            }
        }


        Compilation compilation = CompilationMapper.toEntity(dto, events);
        Compilation saved = compilationRepository.save(compilation);


        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(events);
        Map<Long, Long> viewsMap = getViewsMap(events);

        return CompilationMapper.toDto(saved, confirmedRequestsMap, viewsMap);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }

        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));


        if (request.getTitle() != null &&
                !request.getTitle().equals(compilation.getTitle()) &&
                compilationRepository.existsByTitleAndIdNot(request.getTitle(), compId)) {
            throw new ConflictException(
                    "Compilation with title '" + request.getTitle() + "' already exists"
            );
        }


        Set<Event> events = null;
        if (request.getEvents() != null) {
            if (request.getEvents().isEmpty()) {

                events = new HashSet<>();
            } else {
                events = new HashSet<>(eventRepository.findAllById(request.getEvents()));


                if (events.size() != request.getEvents().size()) {
                    throw new NotFoundException("Some events not found");
                }
            }
        }


        CompilationMapper.updateCompilationFromDto(compilation, request, events);
        Compilation updated = compilationRepository.save(compilation);


        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(updated.getEvents());
        Map<Long, Long> viewsMap = getViewsMap(updated.getEvents());

        return CompilationMapper.toDto(updated, confirmedRequestsMap, viewsMap);
    }

    private Map<Long, Long> getConfirmedRequestsMap(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<Object[]> results = requestRepository.countConfirmedRequestsByEventIds(eventIds);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // eventId
                        row -> (Long) row[1]   // count
                ));
    }

    private Map<Long, Long> getViewsMap(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return Map.of();
        }

        // TODO: Запрос к сервису статистики


        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> 0L
                ));
    }
}
