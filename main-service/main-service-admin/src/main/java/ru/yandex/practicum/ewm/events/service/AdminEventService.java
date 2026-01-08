package ru.yandex.practicum.ewm.events.service;




import ru.yandex.practicum.ewm.dto.EventDto.AdminEventSearchParams;
import ru.yandex.practicum.ewm.dto.EventDto.EventFullDto;
import ru.yandex.practicum.ewm.dto.EventDto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> searchEventsForAdmin(
            AdminEventSearchParams filter);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);
}

