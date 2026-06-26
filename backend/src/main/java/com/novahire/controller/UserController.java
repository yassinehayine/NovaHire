package com.novahire.controller;

import com.novahire.dto.request.ChangePasswordRequest;
import com.novahire.dto.request.UpdateAvatarRequest;
import com.novahire.dto.request.UpdateCvRequest;
import com.novahire.dto.request.UpdateProfileRequest;
import com.novahire.dto.response.ApiResponse;
import com.novahire.dto.response.UserResponse;
import com.novahire.entity.User;
import com.novahire.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("User retrieved", UserResponse.fromUser(user)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse updated = userService.updateProfile(user, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> updateAvatar(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateAvatarRequest request) {
        UserResponse updated = userService.updateAvatar(user, request);
        return ResponseEntity.ok(ApiResponse.success("Avatar updated successfully", updated));
    }

    @PutMapping("/me/cv")
    public ResponseEntity<ApiResponse<Void>> updateCv(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateCvRequest request) {
        userService.updateCvText(user, request.getCvText());
        return ResponseEntity.ok(ApiResponse.success("CV uploaded successfully", null));
    }
}
