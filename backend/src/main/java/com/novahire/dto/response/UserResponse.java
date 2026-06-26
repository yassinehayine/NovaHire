package com.novahire.dto.response;

import com.novahire.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User projection returned to clients.
 * Password is never included. avatarBase64 is included so the frontend
 * can render <img src={avatarBase64}> directly without a second request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    // Sprint 2 profile fields
    private String targetRole;
    private String experienceLevel;
    private Integer yearsOfExperience;
    private String preferredLanguage;
    private String targetCountry;
    private String avatarBase64;
    private boolean hasCv;          // true if cvText is non-null — never send raw CV to client

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .targetRole(user.getTargetRole())
                .experienceLevel(user.getExperienceLevel())
                .yearsOfExperience(user.getYearsOfExperience())
                .preferredLanguage(user.getPreferredLanguage())
                .targetCountry(user.getTargetCountry())
                .avatarBase64(user.getAvatarBase64())
                .hasCv(user.getCvText() != null && !user.getCvText().isBlank())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
