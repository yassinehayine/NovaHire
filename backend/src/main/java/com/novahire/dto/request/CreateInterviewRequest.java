package com.novahire.dto.request;

import com.novahire.entity.Interview;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateInterviewRequest {

    @NotBlank(message = "Target role is required")
    @Size(max = 100)
    private String targetRole;

    @NotNull(message = "Experience level is required")
    private Interview.ExperienceLevel experienceLevel;

    /** List of selected technologies — e.g. ["Java", "Spring Boot"] */
    private List<String> technologies;

    @NotNull(message = "Duration is required")
    private Integer durationMinutes;

    /** ENGLISH | FRENCH | GERMAN */
    @NotBlank(message = "Language is required")
    private String language;
}
