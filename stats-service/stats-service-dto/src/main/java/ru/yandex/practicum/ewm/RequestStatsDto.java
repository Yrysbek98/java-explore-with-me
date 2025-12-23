package ru.yandex.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestStatsDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
