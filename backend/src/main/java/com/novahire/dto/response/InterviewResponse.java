package com.novahire.dto.response;

import com.novahire.entity.Interview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {

    private Long id;
    private String targetRole;
    private String experienceLevel;
    private List<String> technologies;
    private Integer durationMinutes;
    private Integer questionCount;
    private String language;
    private String interviewStyle;
    private String status;
    private Integer score;
    private String overallFeedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public static InterviewResponse fromInterview(Interview i) {
        return InterviewResponse.builder()
                .id(i.getId())
                .targetRole(i.getTargetRole())
                .experienceLevel(i.getExperienceLevel().name())
                .technologies(i.getTechnologies())
                .durationMinutes(i.getDurationMinutes())
                .questionCount(i.getQuestionCount())
                .language(i.getLanguage())
                .interviewStyle(i.getInterviewStyle() != null ? i.getInterviewStyle().name() : "MIXED")
                .status(i.getStatus().name())
                .score(i.getScore())
                .overallFeedback(i.getOverallFeedback())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .completedAt(i.getCompletedAt())
                .build();
    }
}
