package com.novahire.service;

import com.novahire.dto.request.CreateInterviewRequest;
import com.novahire.dto.response.DashboardStatsResponse;
import com.novahire.dto.response.InterviewResponse;
import com.novahire.entity.Interview;
import com.novahire.entity.User;
import com.novahire.exception.ForbiddenException;
import com.novahire.exception.ResourceNotFoundException;
import com.novahire.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final InterviewRepository interviewRepository;

    /**
     * Creates a new interview session and persists it.
     *
     * <p>questionCount is derived here so Sprint 3 GeminiService can read it
     * directly from the entity without re-computing it. Formula: 1 question
     * per 5 minutes of planned duration (clamped to 3–15 questions).
     */
    @Transactional
    public InterviewResponse createInterview(User user, CreateInterviewRequest request) {
        int questionCount = computeQuestionCount(request.getDurationMinutes());

        Interview interview = Interview.builder()
                .user(user)
                .targetRole(request.getTargetRole().trim())
                .experienceLevel(request.getExperienceLevel())
                .technologies(request.getTechnologies() != null ? request.getTechnologies() : List.of())
                .durationMinutes(request.getDurationMinutes())
                .questionCount(questionCount)
                .language(request.getLanguage().toUpperCase())
                .interviewStyle(request.getInterviewStyle() != null
                        ? request.getInterviewStyle()
                        : Interview.InterviewStyle.MIXED)
                .status(Interview.Status.CREATED)
                .build();

        Interview saved = interviewRepository.save(interview);
        log.info("Interview created id={} for user id={}", saved.getId(), user.getId());
        return InterviewResponse.fromInterview(saved);
    }

    /**
     * Returns aggregated stats for the authenticated user's dashboard.
     * Every number comes from the database — nothing is fabricated.
     */
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(User user) {
        Long userId = user.getId();

        long total       = interviewRepository.countByUserId(userId);
        long completed   = interviewRepository.countByUserIdAndStatus(userId, Interview.Status.COMPLETED);
        long inProgress  = interviewRepository.countByUserIdAndStatus(userId, Interview.Status.IN_PROGRESS);
        Double avgScore  = interviewRepository.findAverageScoreByUserId(userId);
        Integer bestScore= interviewRepository.findBestScoreByUserId(userId);

        List<InterviewResponse> recent = interviewRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(InterviewResponse::fromInterview)
                .toList();

        return DashboardStatsResponse.builder()
                .totalInterviews(total)
                .completedInterviews(completed)
                .inProgressInterviews(inProgress)
                .averageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : null)
                .bestScore(bestScore)
                .recentInterviews(recent)
                .build();
    }

    @Transactional(readOnly = true)
    public List<InterviewResponse> getUserInterviews(User user) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(InterviewResponse::fromInterview)
                .toList();
    }

    @Transactional(readOnly = true)
    public InterviewResponse getInterviewById(User user, Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", interviewId));

        // Ownership guard — prevents horizontal privilege escalation
        if (!interview.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this interview");
        }

        return InterviewResponse.fromInterview(interview);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /**
     * 1 question per 5 minutes, clamped between 3 and 15.
     * Examples: 15m → 3q, 30m → 6q, 60m → 12q, 90m → 15q (capped)
     */
    private int computeQuestionCount(int durationMinutes) {
        int raw = durationMinutes / 5;
        return Math.max(3, Math.min(15, raw));
    }
}
