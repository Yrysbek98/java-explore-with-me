package ru.yandex.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.events.dto.EventFullDto;
import ru.yandex.practicum.ewm.events.dto.EventSearchFilter;
import ru.yandex.practicum.ewm.events.dto.EventShortDto;
import ru.yandex.practicum.ewm.events.mapper.EventMapper;
import ru.yandex.practicum.ewm.exception.exceptionType.NotFoundException;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
   // private final StatisticsService statisticsService;

    public List<EventShortDto> getAllEvents(EventSearchFilter filter) {

        LocalDateTime rangeStart = filter.getRangeStart();
        LocalDateTime rangeEnd = filter.getRangeEnd();

        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
        }


        Sort sort = Sort.by("eventDate").ascending(); // по умолчанию
        if (filter.getSort() != null) {
            if (filter.getSort().equalsIgnoreCase("VIEWS")) {
                sort = Sort.by("views").descending();
            } else if (filter.getSort().equalsIgnoreCase("EVENT_DATE")) {
                sort = Sort.by("eventDate").ascending();
            }
        }

        PageRequest pageable = PageRequest.of(
                filter.getFrom() / filter.getSize(),
                filter.getSize(),
                sort
        );

        Page<Event> events = eventRepository.searchEventsForPublic(
                filter.getText(),
                filter.getCategories(),
                filter.getPaid(),
                rangeStart,
                rangeEnd,
                filter.getOnlyAvailable(),
                pageable
        );


      //  statisticsService.saveEndpointHit("/events", "PUBLIC_SEARCH");

        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto getEventById(Long eventId) {


        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие не найдено или не опубликовано"));


        event.setViews(event.getViews() + 1);
        eventRepository.save(event);


        //  statisticsService.saveEndpointHit("/events/" + eventId, "GET_EVENT");

        return eventMapper.toFullDto(event);
    }
}
