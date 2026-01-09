package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.ResponseStatsDto;
import ru.yandex.practicum.ewm.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stats, Long> {


    @Query("""
            SELECT new ru.practicum.stats.dto.ResponseStatsDto(s.app, s.uri, COUNT(DISTINCT s.ip))
            FROM Stats s
            WHERE s.timestamp BETWEEN :start AND :end
            AND (COALESCE(:uris, NULL) IS NULL OR s.uri IN :uris)
            GROUP BY s.app, s.uri
            ORDER BY COUNT(DISTINCT s.ip) DESC
            """)
    List<ResponseStatsDto> findUniqueStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );


    @Query("""
            SELECT new ru.practicum.stats.dto.ResponseStatsDto(s.app, s.uri, COUNT(s.ip))
            FROM Stats s
            WHERE s.timestamp BETWEEN :start AND :end
            AND (COALESCE(:uris, NULL) IS NULL OR s.uri IN :uris)
            GROUP BY s.app, s.uri
            ORDER BY COUNT(s.ip) DESC
            """)
    List<ResponseStatsDto> findAllStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}
