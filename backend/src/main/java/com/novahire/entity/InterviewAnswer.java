package com.novahire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * The user's answer to one {@link InterviewQuestion} — exactly one per question (DB-enforced
 * via the unique {@code question_id} column), created on first auto-save and upserted after.
 *
 * <p>{@code score}/{@code aiFeedback} are pre-added so Sprint 4's per-question AI evaluation
 * is a pure addition — they stay null until then, mirroring {@link Interview#getScore()} /
 * {@link Interview#getOverallFeedback()} already added in Sprint 2 at the session level.
 */
@Entity
@Table(name = "interview_answers", indexes = {
    @Index(name = "idx_answer_interview_id", columnList = "interview_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private InterviewQuestion question;

    /** Denormalized — avoids a join through {@code question} for "all answers in this session" queries. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @Column(columnDefinition = "TEXT")
    private String answerText;

    /** Client-computed UX metric — how long the user spent on this question. */
    @Column
    private Integer timeSpentSeconds;

    /** Null until Sprint 4 evaluates this answer. */
    @Column
    private Integer score;

    /** Null until Sprint 4 evaluates this answer. */
    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    /** Overwritten on every auto-save. */
    @Column
    private LocalDateTime answeredAt;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
