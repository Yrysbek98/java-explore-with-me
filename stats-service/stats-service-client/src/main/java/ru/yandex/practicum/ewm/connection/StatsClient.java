package ru.yandex.practicum.ewm.connection;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.ResponseStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;



@Service
@RequiredArgsConstructor
public class StatsClient {
    private  final  RestTemplate restTemplate;

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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> builder.queryParam("uris", uri));
        }

        if (unique != null) {
            builder.queryParam("unique", unique);
        }

        return restTemplate.getForEntity(
                builder.toUriString(),
                ResponseStatsDto[].class
        );
    }
}

