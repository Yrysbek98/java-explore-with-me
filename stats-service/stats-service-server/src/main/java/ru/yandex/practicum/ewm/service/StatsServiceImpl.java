package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.ewm.RequestStatsDto;
import ru.yandex.practicum.ewm.ResponseStatsDto;
import ru.yandex.practicum.ewm.mapper.StatsMapper;
import ru.yandex.practicum.ewm.model.Stats;
import ru.yandex.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void saveHit(RequestStatsDto dto) {
        Stats stats = StatsMapper.toEntity(dto);
        statsRepository.save(stats);
    }

    @Override
    public List<ResponseStatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    ) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.findUniqueStats(start, end, uris);
        } else {
            return statsRepository.findAllStats(start, end, uris);
        }
    }
}
