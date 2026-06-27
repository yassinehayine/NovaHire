package com.novahire.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveAnswerRequest {

    @Size(max = 10_000, message = "Answer must be at most 10,000 characters")
    private String answerText;

    @Min(value = 0, message = "Time spent cannot be negative")
    private Integer timeSpentSeconds;
}
