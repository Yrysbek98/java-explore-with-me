package ru.yandex.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.ewm.model.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);


    @Query("SELECT c FROM Compilation c " +
            "WHERE :pinned IS NULL OR c.pinned = :pinned")
    Page<Compilation> findByPinnedFilter(@Param("pinned") Boolean pinned, Pageable pageable);
}
