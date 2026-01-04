package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
     Page<Event> findByInitiator_idOrderByStartDesc(Long userId);
}
