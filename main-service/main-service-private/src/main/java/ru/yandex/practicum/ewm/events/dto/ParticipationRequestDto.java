package ru.yandex.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.ewm.enums.EventState;


import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private LocalDateTime created;
    private Integer event;
    private Integer id;
    private Integer requester;
    private EventState state;

}
