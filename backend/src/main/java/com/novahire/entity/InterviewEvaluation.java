package com.novahire.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One AI-generated evaluation report per {@link Interview} — the Sprint 5 counterpart to the
 * question set produced in Sprint 4. Created (or reset) by the evaluation engine after an
 * interview reaches {@link Interview.Status#COMPLETED}.
 *
 * <p>Deliberately separate from {@link InterviewAnswer}: answers stay the raw candidate input,
 * while this table plus {@link QuestionEvaluation} hold only <em>normalized</em> derived data —
 * never the raw provider response.
 *
 * <p>Idempotency & retry are driven by {@link #status}:
 * <pre>
 *   PENDING → IN_PROGRESS → COMPLETED
 *                   ↓
 *                 FAILED  → (manual retry) → IN_PROGRESS → …
 * </pre>
 * The evaluation engine only calls the AI provider when status is absent or FAILED; a COMPLETED
 * row is returned as-is and never re-generated.
 */
@Entity
@Table(name = "interview_evaluations", indexes = {
    @Index(name = "idx_evaluation_interview_id", columnList = "interview_id"),
    @Index(name = "idx_evaluation_status",       columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** One report per interview — DB-enforced via the unique {@code interview_id} column. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private Interview interview;

    @OneToMany(mappedBy = "interviewEvaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<QuestionEvaluation> questionEvaluations = new ArrayList<>();

    // ── State machine ──────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PENDING;

    // ── Report scores (0–100, null until COMPLETED) ───────────────────────────

    /** Aggregate 0–100; mirrored into {@link Interview#getScore()} on completion. */
    @Column
    private Integer overallScore;

    @Column
    private Integer technicalScore;

    @Column
    private Integer communicationScore;

    @Column
    private Integer problemSolvingScore;

    @Column
    private Integer confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Recommendation recommendation;

    // ── Normalized narrative (element-collection lists, not raw blobs) ─────────

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evaluation_strengths",
            joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "item", columnDefinition = "TEXT")
    @OrderColumn(name = "position")
    @Builder.Default
    private List<String> strengths = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evaluation_weaknesses",
            joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "item", columnDefinition = "TEXT")
    @OrderColumn(name = "position")
    @Builder.Default
    private List<String> weaknesses = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "evaluation_roadmap",
            joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "item", columnDefinition = "TEXT")
    @OrderColumn(name = "position")
    @Builder.Default
    private List<String> improvementRoadmap = new ArrayList<>();

    /** Short professional narrative; mirrored into {@link Interview#getOverallFeedback()}. */
    @Column(columnDefinition = "TEXT")
    private String summary;

    // ── Retry / failure tracking ───────────────────────────────────────────────

    /** Number of times evaluation has been attempted (each attempt = up to 1 retry inside). */
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;

    /** Populated on FAILED — human-readable reason for the last failure. */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    // ── Audit (which provider/model/prompt produced this) ─────────────────────

    /** e.g. "gemini" — {@link com.novahire.ai.client.AIProviderClient#providerName()}. */
    @Column(length = 50)
    private String provider;

    /** e.g. "gemini-2.5-flash-lite". */
    @Column(length = 100)
    private String model;

    /** Prompt template version at generation time (novahire.ai.prompt.version). */
    @Column(length = 20)
    private String promptVersion;

    /**
     * Evaluation-engine schema version — bumped when the evaluation prompt/logic evolves so we
     * can tell which generation of the engine produced a stored report (independent of the
     * provider prompt template version).
     */
    @Column(length = 20)
    private String evaluationVersion;

    // ── Timestamps ─────────────────────────────────────────────────────────────

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /** Set when status transitions to COMPLETED. */
    @Column
    private LocalDateTime evaluatedAt;

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /** Replaces the child collection while keeping both sides of the relationship consistent. */
    public void replaceQuestionEvaluations(List<QuestionEvaluation> items) {
        this.questionEvaluations.clear();
        for (QuestionEvaluation qe : items) {
            qe.setInterviewEvaluation(this);
            this.questionEvaluations.add(qe);
        }
    }

    // ── Enums ──────────────────────────────────────────────────────────────────

    public enum Status {
        PENDING,      // row created, not yet run
        IN_PROGRESS,  // AI call in flight
        COMPLETED,    // stored, never regenerated
        FAILED        // AI failed after retry — manual retry allowed
    }

    public enum Recommendation {
        STRONG_HIRE("Strong Hire"),
        HIRE("Hire"),
        BORDERLINE("Borderline"),
        NO_HIRE("No Hire");

        private final String label;

        Recommendation(String label) { this.label = label; }

        public String getLabel() { return label; }

        /** Lenient parse for AI output — defaults to BORDERLINE on anything unrecognized. */
        public static Recommendation fromString(String raw) {
            if (raw == null) return BORDERLINE;
            String norm = raw.trim().toUpperCase().replace(' ', '_').replace('-', '_');
            for (Recommendation r : values()) {
                if (r.name().equals(norm) || r.label.equalsIgnoreCase(raw.trim())) return r;
            }
            return BORDERLINE;
        }
    }
}
