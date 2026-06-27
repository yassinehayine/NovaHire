package com.novahire.dto.response;

import com.novahire.entity.InterviewAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * score/aiFeedback are deliberately omitted — they're always null in Sprint 3.
 * Adding them in Sprint 4 is a pure addition to this DTO, not a change to existing fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewAnswerResponse {

    private Long questionId;
    private String answerText;
    private Integer timeSpentSeconds;
    private LocalDateTime answeredAt;

    public static InterviewAnswerResponse fromAnswer(InterviewAnswer a) {
        return InterviewAnswerResponse.builder()
                .questionId(a.getQuestion().getId())
                .answerText(a.getAnswerText())
                .timeSpentSeconds(a.getTimeSpentSeconds())
                .answeredAt(a.getAnsweredAt())
                .build();
    }
}
