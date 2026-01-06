package ru.yandex.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchFilter {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    @Builder.Default
    private Boolean onlyAvailable = false;
    private String sort;
    @Builder.Default
    private Integer from = 0;
    @Builder.Default
    private Integer size = 10;
}
