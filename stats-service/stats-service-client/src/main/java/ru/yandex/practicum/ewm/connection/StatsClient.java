package ru.yandex.practicum.ewm.connection;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.ResponseStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate;

    @Value("${stats-service.url}")
    private String serverUrl;


    public void saveHit(RequestStatsDto dto) {
        restTemplate.postForEntity(
                serverUrl + "/hit",
                dto,
                Void.class
        );
    }


    public ResponseEntity<ResponseStatsDto[]> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        StringBuilder urlBuilder = new StringBuilder(serverUrl + "/stats?start={start}&end={end}");

        if (uris != null && !uris.isEmpty()) {
            params.put("uris", String.join(",", uris));
            urlBuilder.append("&uris={uris}");
        }

        if (unique != null) {
            params.put("unique", unique);
            urlBuilder.append("&unique={unique}");
        }

        return restTemplate.getForEntity(
                urlBuilder.toString(),
                ResponseStatsDto[].class,
                params
        );
    }
}

