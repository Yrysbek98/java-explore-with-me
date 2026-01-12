package ru.yandex.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.ewm.model.CommentRating;

import java.util.Optional;

public interface CommentRatingRepository extends JpaRepository<CommentRating, Long> {

    Optional<CommentRating> findByCommentIdAndUserId(Long commentId, Long userId);

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Long countByCommentIdAndIsHelpful(Long commentId, Boolean isHelpful);
}
