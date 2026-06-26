package com.novahire.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 100)
    private String targetRole;

    /** JUNIOR | MID | SENIOR | LEAD */
    private String experienceLevel;

    private Integer yearsOfExperience;

    /** ENGLISH | FRENCH | GERMAN */
    private String preferredLanguage;

    /** GERMANY | FRANCE | CANADA | MOROCCO | ... */
    private String targetCountry;
}
