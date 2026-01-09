package ru.yandex.practicum.ewm.events.service;


import ru.yandex.practicum.ewm.dto.AdminEventSearchParams;
import ru.yandex.practicum.ewm.dto.EventFullDto;
import ru.yandex.practicum.ewm.dto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> searchEventsForAdmin(
            AdminEventSearchParams filter);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);
}

