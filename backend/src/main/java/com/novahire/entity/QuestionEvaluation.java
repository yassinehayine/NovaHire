package com.novahire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * The AI's verdict on one answered (or skipped) {@link InterviewQuestion} — exactly one per
 * question within an evaluation. Anchored to the question (which always exists) rather than to
 * {@link InterviewAnswer} (which may be absent when the candidate skipped), so a skipped
 * question can still receive a {@code score=0} verdict.
 *
 * <p>Holds only normalized derived feedback; the raw candidate answer stays in
 * {@link InterviewAnswer} and the model answer key stays in {@link InterviewQuestion}.
 */
@Entity
@Table(name = "question_evaluations", indexes = {
    @Index(name = "idx_qeval_evaluation_id", columnList = "evaluation_id"),
    @Index(name = "idx_qeval_question_id",   columnList = "question_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private InterviewEvaluation interviewEvaluation;

    /** One verdict per question — DB-enforced via the unique {@code question_id} column. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private InterviewQuestion question;

    /** Per-answer score on a 0–10 scale. */
    @Column(nullable = false)
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String improvementSuggestions;

    /** Short AI explanation of the score. */
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
