package com.novahire.controller;

import com.novahire.dto.request.CreateInterviewRequest;
import com.novahire.dto.response.ApiResponse;
import com.novahire.dto.response.DashboardStatsResponse;
import com.novahire.dto.response.InterviewResponse;
import com.novahire.entity.User;
import com.novahire.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewResponse>> createInterview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateInterviewRequest request) {
        InterviewResponse response = interviewService.createInterview(user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getUserInterviews(
            @AuthenticationPrincipal User user) {
        List<InterviewResponse> interviews = interviewService.getUserInterviews(user);
        return ResponseEntity.ok(ApiResponse.success("Interviews retrieved", interviews));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InterviewResponse>> getInterview(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        InterviewResponse response = interviewService.getInterviewById(user, id);
        return ResponseEntity.ok(ApiResponse.success("Interview retrieved", response));
    }

    @GetMapping("/stats/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal User user) {
        DashboardStatsResponse stats = interviewService.getDashboardStats(user);
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
    }
}
