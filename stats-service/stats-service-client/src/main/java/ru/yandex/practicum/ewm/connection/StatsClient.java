package ru.yandex.practicum.ewm.connection;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.ResponseStatsDto;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate;

    @Value("${stats-service-server.url}")
    private String serverUrl;


    public ResponseEntity<Object> addNewData(RequestStatsDto dto, @PathVariable Long id) {
        HttpHeaders headers = createHeaders(id);
        HttpEntity<RequestStatsDto> requestEntity = new HttpEntity<>(dto, headers);
        return restTemplate.exchange(
                serverUrl + "/hit",
                HttpMethod.POST,
                requestEntity,
                Object.class
        );
    }

    public ResponseEntity<Object> getData(RequestStatsDto dto, @PathVariable Long id, LocalDateTime start, LocalDateTime end) {
        HttpHeaders headers = createHeaders(id);
        HttpEntity<RequestStatsDto> requestEntity = new HttpEntity<>(dto, headers);
        return restTemplate.exchange(
                serverUrl + "/stats/" + start + end,
                HttpMethod.GET,
                requestEntity,
                Object.class
        );
    }

    private HttpHeaders createHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}
