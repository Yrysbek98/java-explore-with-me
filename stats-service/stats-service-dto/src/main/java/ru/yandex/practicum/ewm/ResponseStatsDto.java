package ru.yandex.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseStatsDto {
    private String app;
    private String uri;
    private int hit;
}
