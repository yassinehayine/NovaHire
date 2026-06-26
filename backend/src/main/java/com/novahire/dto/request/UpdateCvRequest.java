package com.novahire.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCvRequest {
    @NotBlank(message = "CV text cannot be empty")
    private String cvText;
}
