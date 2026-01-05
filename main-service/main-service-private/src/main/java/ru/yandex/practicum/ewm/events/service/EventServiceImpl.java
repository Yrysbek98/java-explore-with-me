package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.events.dto.*;
import ru.yandex.practicum.ewm.events.mapper.EventMapper;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.exception.exceptionType.ValidationException;
import ru.yandex.practicum.ewm.model.Category;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CategoryRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> eventPage = eventRepository.findByInitiatorId(userId, pageable);

        return eventPage.getContent()
                .stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto addNewEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь не найдено")
                );
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() ->
                        new NotFoundException("Категория не найдена")
                );

        Event event = EventMapper.toEntityFromNewDto(dto, user, category);
        if (!eventRepository.isEventDateAtLeastTwoHoursAfterCreated(event.getId())) {
            throw new ValidationException("Неправильно указана дата");
        }
        Event saved = eventRepository.save(event);
        return EventMapper.toEventFullDto(saved);
    }

    @Override
    public EventFullDto getUsersEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие не найдено")
        );

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь  не найден")
                );

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие не найдено")
        );

        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("Событие уже опубликовано");
        }

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() ->
                        new NotFoundException("Категория не найдена")
                );

        Event update = EventMapper.toEntityFromUpdateDto(eventId, dto, user, category);

        Event saved = eventRepository.save(update);

        return EventMapper.toEventFullDto(saved);
    }

    @Override
    public ParticipationRequestDto getUserEventRequest(Long userId, Long eventId) {
        return null;
    }

    @Override
    public void updateUserEventRequest(Long userId, Long eventId) {

    }
}
