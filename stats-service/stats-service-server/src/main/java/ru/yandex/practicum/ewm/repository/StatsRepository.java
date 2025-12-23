package ru.yandex.practicum.ewm.repository;

import ru.yandex.practicum.ewm.model.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<Stats, Long> {
}
