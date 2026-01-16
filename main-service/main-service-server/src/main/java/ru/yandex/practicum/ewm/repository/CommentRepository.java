package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.ewm.enums.CommentStatus;
import ru.yandex.practicum.ewm.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAuthorId(Long authorId, Sort sort);

    List<Comment> findByStatus(CommentStatus status, Sort sort);

    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId AND c.status = :status " +
            "ORDER BY c.helpfulCount DESC, c.createdOn DESC")
    List<Comment> findByEventIdAndStatusOrderByRating(
            @Param("eventId") Long eventId,
            @Param("status") CommentStatus status
    );
}
