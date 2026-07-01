package com.novahire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * One question belonging to a specific interview session.
 *
 * <p>Sprint 3 — populated by {@code StaticQuestionGenerationStrategy} at session start.
 * <p>Sprint 4 — {@code source=AI_GENERATED} rows populated by a Gemini-backed strategy instead.
 *
 * <p>Each session stores its own copies of question text (not a shared catalog row) so that
 * editing the static bank later never retroactively changes an already-completed session.
 */
@Entity
@Table(name = "interview_questions", indexes = {
    @Index(name = "idx_question_interview_id", columnList = "interview_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    /** 0-based position — stable order for Prev/Next navigation. */
    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionCategory category;

    /** Reuses {@link Interview.ExperienceLevel} — no separate difficulty axis needed yet. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Interview.ExperienceLevel difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private QuestionSource source = QuestionSource.STATIC;

    /**
     * Bank key (static) or promptId UUID (AI-generated) — for debugging/traceability only.
     * Never sent to clients.
     */
    @Column(length = 50)
    private String sourceRef;

    /**
     * Model answer key points stored by the AI at generation time.
     * Null for static questions. Used for future automated scoring (Sprint 5+).
     */
    @Column(columnDefinition = "TEXT")
    private String expectedAnswer;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum QuestionCategory {
        // Sprint 3 (existing — no existing rows affected)
        TECHNICAL,
        BEHAVIORAL,
        // Sprint 4 (new — AI uses these based on interview style)
        PROBLEM_SOLVING,
        SYSTEM_DESIGN,
        ALGORITHMS,
        COMMUNICATION,
        ARCHITECTURE,
        DEBUGGING,
        CODING
    }

    public enum QuestionSource {
        STATIC, AI_GENERATED
    }
}
