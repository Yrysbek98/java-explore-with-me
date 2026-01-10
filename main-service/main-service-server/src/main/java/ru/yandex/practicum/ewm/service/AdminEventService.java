package ru.yandex.practicum.ewm.service;


import ru.yandex.practicum.ewm.dto.*;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> searchEventsForAdmin(
            AdminEventSearchParams filter);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);
}

