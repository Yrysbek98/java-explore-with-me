package ru.yandex.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.categories.dto.CategoryDto;
import ru.yandex.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
