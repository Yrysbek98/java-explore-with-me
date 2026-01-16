package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewm.enums.CommentStatus;
import ru.yandex.practicum.ewm.model.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByAuthorId(Long userId, Pageable pageable);

    Page<Comment> findByStatus(CommentStatus status, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId AND c.status = :status " +
            "ORDER BY c.helpfulCount DESC, c.createdOn DESC")
    Page<Comment> findByEventIdAndStatusOrderByRating(
            @Param("eventId") Long eventId,
            @Param("status") CommentStatus status,
            Pageable pageable
    );
}
