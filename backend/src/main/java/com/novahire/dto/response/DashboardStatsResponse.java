package com.novahire.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aggregated statistics for the dashboard.
 * All values are computed server-side — never faked on the frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private long totalInterviews;
    private long completedInterviews;
    private long inProgressInterviews;

    /** Null if no completed interviews with scores yet. */
    private Double averageScore;

    /** Null if no completed interviews with scores yet. */
    private Integer bestScore;

    /** Last 5 interviews for the "recent activity" widget. */
    private List<InterviewResponse> recentInterviews;
}
