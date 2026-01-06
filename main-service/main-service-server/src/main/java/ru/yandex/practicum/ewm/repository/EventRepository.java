package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.enums.EventState;
import ru.yandex.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;
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

        @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM events
                WHERE id = :eventId
                  AND event_date >= created_on + INTERVAL '1 hour'
            )
        """, nativeQuery = true)
        boolean isEventDateAtLeastOneHourAfterCreated(
                @Param("eventId") Long eventId
        );

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
    Page<Event> searchEvents(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );
}
