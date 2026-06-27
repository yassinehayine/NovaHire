package com.novahire.repository;

import com.novahire.entity.Interview;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Row-locking read used only by the session lazy-start (CREATED -> IN_PROGRESS) path.
     * Holding this lock for the transaction's duration serializes concurrent loadSession()
     * calls for the same interview, so a second caller blocks until the first commits and
     * then re-reads the now-IN_PROGRESS row instead of generating a duplicate question set.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Interview i WHERE i.id = :id")
    Optional<Interview> findByIdForUpdate(@Param("id") Long id);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, Interview.Status status);

    @Query("SELECT AVG(i.score) FROM Interview i WHERE i.user.id = :userId AND i.score IS NOT NULL")
    Double findAverageScoreByUserId(Long userId);

    @Query("SELECT MAX(i.score) FROM Interview i WHERE i.user.id = :userId AND i.score IS NOT NULL")
    Integer findBestScoreByUserId(Long userId);

    List<Interview> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
}
