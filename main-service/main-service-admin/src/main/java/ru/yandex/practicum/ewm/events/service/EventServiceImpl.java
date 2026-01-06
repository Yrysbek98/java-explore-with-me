package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventSearchFilter;
import ru.yandex.practicum.ewm.events.dto.UpdateEventDto;
import ru.yandex.practicum.ewm.events.mapper.EventMapper;
import ru.yandex.practicum.ewm.exception.exceptionType.ConflictException;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.exception.exceptionType.ValidationException;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventFullDto> getAllEvents(
            EventSearchFilter filter) {

        if (filter.getRangeStart() != null && filter.getRangeEnd() != null) {
            if (filter.getRangeStart().isAfter(filter.getRangeEnd())) {
                throw new ValidationException("rangeStart не может быть позже rangeEnd");
            }
        }

        List<EventState> eventStates = null;
        if (filter.getStates() != null && !filter.getStates().isEmpty()) {
            eventStates = filter.getStates().stream()
                    .map(state -> {
                        try {
                            return EventState.valueOf(state);
                        } catch (IllegalArgumentException e) {
                            throw new ValidationException("Неизвестное состояние события: " + state);
                        }
                    })
                    .collect(Collectors.toList());
        }

        Pageable pageable = PageRequest.of(filter.getFrom() / filter.getSize(), filter.getSize());

        Page<Event> events = eventRepository.searchEvents(
                filter.getUsers(),
                eventStates,
                filter.getCategories(),
                filter.getRangeStart(),
                filter.getRangeEnd(),
                pageable
        );

        return events.getContent().stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }


    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }


        if (event.getEventDate() != null) {
            LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
            if (event.getEventDate().isBefore(oneHourFromNow)) {
                throw new ConflictException("Дата начала события должна быть не ранее чем за час от текущего момента");
            }
        }


        if (dto.getStateAction() != null) {
            if ("PUBLISH_EVENT".equals(dto.getStateAction())) {

                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());

            } else if ("REJECT_EVENT".equals(dto.getStateAction())) {

                if (event.getState() == EventState.PUBLISHED) {
                    throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано");
                }
                event.setState(EventState.CANCELED);
            }
        }

        Event saved = eventRepository.save(event);
        return EventMapper.toEventFullDto(saved);
    }
}
