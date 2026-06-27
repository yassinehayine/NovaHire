package com.novahire.dto.response;

import com.novahire.entity.InterviewQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionResponse {

    private Long id;
    private Integer orderIndex;
    private String text;
    private String category;
    private String difficulty;

    public static InterviewQuestionResponse fromQuestion(InterviewQuestion q) {
        return InterviewQuestionResponse.builder()
                .id(q.getId())
                .orderIndex(q.getOrderIndex())
                .text(q.getText())
                .category(q.getCategory().name())
                .difficulty(q.getDifficulty().name())
                .build();
    }
}
