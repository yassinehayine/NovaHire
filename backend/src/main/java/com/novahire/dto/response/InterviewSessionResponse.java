package com.novahire.dto.response;

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
public class InterviewSessionResponse {

    private InterviewResponse interview;
    private List<InterviewQuestionResponse> questions;
    private List<InterviewAnswerResponse> answers;
    private LocalDateTime startedAt;
    private Integer durationMinutes;
}
