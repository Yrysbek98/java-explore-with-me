package ru.yandex.practicum.ewm.events.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.ewm.dto.EventDto.*;
import ru.yandex.practicum.ewm.dto.RequestDto.ParticipationRequestDto;
import ru.yandex.practicum.ewm.events.service.PrivateEventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {

    private final PrivateEventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto dto) {
        return eventService.addEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest request) {
        return eventService.updateUserEvent(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return eventService.updateEventRequestStatus(userId, eventId, request);
    }
}
