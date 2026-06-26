package com.novahire.service;

import com.novahire.dto.request.ChangePasswordRequest;
import com.novahire.dto.request.UpdateAvatarRequest;
import com.novahire.dto.request.UpdateProfileRequest;
import com.novahire.dto.response.UserResponse;
import com.novahire.entity.User;
import com.novahire.exception.BadRequestException;
import com.novahire.exception.EmailAlreadyExistsException;
import com.novahire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Updates editable profile fields.
     * Email uniqueness is re-checked only if the email actually changed.
     */
    @Transactional
    public UserResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        boolean emailChanged = !currentUser.getEmail().equalsIgnoreCase(request.getEmail());
        if (emailChanged && userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        currentUser.setFirstName(request.getFirstName().trim());
        currentUser.setLastName(request.getLastName().trim());
        currentUser.setEmail(request.getEmail().trim().toLowerCase());
        currentUser.setTargetRole(request.getTargetRole());
        currentUser.setExperienceLevel(request.getExperienceLevel());
        currentUser.setYearsOfExperience(request.getYearsOfExperience());
        currentUser.setPreferredLanguage(request.getPreferredLanguage());
        currentUser.setTargetCountry(request.getTargetCountry());

        User saved = userRepository.save(currentUser);
        log.info("Profile updated for user id={}", saved.getId());
        return UserResponse.fromUser(saved);
    }

    /**
     * Validates current password before setting the new one.
     * confirmPassword check is done here — not in the DTO — so the error
     * message is consistent and testable at the service layer.
     */
    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New passwords do not match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), currentUser.getPassword())) {
            throw new BadRequestException("New password must be different from the current one");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        log.info("Password changed for user id={}", currentUser.getId());
    }

    /**
     * Stores avatar as a Base64 data-URL.
     * Basic size guard: a 2MB image in base64 is ~2.7MB of text.
     * Spring's max-request-size is the outer guard; this is an inner sanity check.
     */
    @Transactional
    public UserResponse updateAvatar(User currentUser, UpdateAvatarRequest request) {
        String data = request.getAvatarBase64();

        if (!data.startsWith("data:image/")) {
            throw new BadRequestException("Invalid image format. Only JPEG, PNG, and WebP are accepted.");
        }
        // ~3.6MB max in base64 ≈ 2.7MB binary
        if (data.length() > 3_600_000) {
            throw new BadRequestException("Avatar image must be smaller than 2MB.");
        }

        currentUser.setAvatarBase64(data);
        User saved = userRepository.save(currentUser);
        log.info("Avatar updated for user id={}", saved.getId());
        return UserResponse.fromUser(saved);
    }

    /**
     * Stores extracted CV text.
     * The frontend extracts text from the PDF client-side (using pdf.js)
     * and sends plain text. This keeps the backend simple and avoids
     * binary PDF parsing dependencies until Sprint 7 if needed.
     */
    @Transactional
    public void updateCvText(User currentUser, String cvText) {
        if (cvText == null || cvText.isBlank()) {
            throw new BadRequestException("CV text cannot be empty");
        }
        // 50 000 chars ~ 8–10 page CV
        if (cvText.length() > 50_000) {
            throw new BadRequestException("CV text is too long. Maximum is 50 000 characters.");
        }

        currentUser.setCvText(cvText.trim());
        userRepository.save(currentUser);
        log.info("CV text updated for user id={}", currentUser.getId());
    }
}
