package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.model.Event;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM events
            WHERE id = :eventId
              AND event_date >= created_on + INTERVAL '2 hours'
        )
    """, nativeQuery = true)
    boolean isEventDateAtLeastTwoHoursAfterCreated(
            @Param("eventId") Long eventId
    );

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);
}
