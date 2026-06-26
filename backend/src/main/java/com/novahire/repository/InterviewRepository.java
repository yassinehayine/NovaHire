package com.novahire.repository;

import com.novahire.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, Interview.Status status);

    @Query("SELECT AVG(i.score) FROM Interview i WHERE i.user.id = :userId AND i.score IS NOT NULL")
    Double findAverageScoreByUserId(Long userId);

    @Query("SELECT MAX(i.score) FROM Interview i WHERE i.user.id = :userId AND i.score IS NOT NULL")
    Integer findBestScoreByUserId(Long userId);

    List<Interview> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
}
