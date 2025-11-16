package com.taxiservice.controller.member;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.member.ApplicationReviewRequest;
import com.taxiservice.dto.member.MembershipApplicationRequest;
import com.taxiservice.dto.member.MembershipApplicationResponse;
import com.taxiservice.service.member.MembershipApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership-applications")
@RequiredArgsConstructor
@Slf4j
public class MembershipApplicationController {

    private final MembershipApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<MembershipApplicationResponse>> submitApplication(
            @Valid @RequestBody MembershipApplicationRequest request) {
        log.info("REST request to submit membership application for: {}", request.getApplicantName());
        MembershipApplicationResponse response = applicationService.submitApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted successfully", response));
    }

    @PatchMapping("/{applicationId}/secretary-review")
    public ResponseEntity<ApiResponse<MembershipApplicationResponse>> secretaryReview(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationReviewRequest request) {
        log.info("REST request for secretary review of application ID: {}", applicationId);
        MembershipApplicationResponse response = applicationService.secretaryReview(applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Secretary review completed", response));
    }

    @PatchMapping("/{applicationId}/chairperson-review")
    public ResponseEntity<ApiResponse<MembershipApplicationResponse>> chairpersonReview(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationReviewRequest request) {
        log.info("REST request for chairperson review of application ID: {}", applicationId);
        MembershipApplicationResponse response = applicationService.chairpersonReview(applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Chairperson review completed", response));
    }

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApiResponse<MembershipApplicationResponse>> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status) {
        log.info("REST request to update application ID {} to status: {}", applicationId, status);
        MembershipApplicationResponse response = applicationService.updateApplicationStatus(applicationId, status);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", response));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponse<MembershipApplicationResponse>> getApplicationById(@PathVariable Long applicationId) {
        log.info("REST request to get application with ID: {}", applicationId);
        MembershipApplicationResponse response = applicationService.getApplicationById(applicationId);
        return ResponseEntity.ok(ApiResponse.success("Application retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MembershipApplicationResponse>>> getAllApplications() {
        log.info("REST request to get all applications");
        List<MembershipApplicationResponse> responses = applicationService.getAllApplications();
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", responses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MembershipApplicationResponse>>> getApplicationsByStatus(@PathVariable String status) {
        log.info("REST request to get applications by status: {}", status);
        List<MembershipApplicationResponse> responses = applicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", responses));
    }

    @GetMapping("/pending/secretary")
    public ResponseEntity<ApiResponse<List<MembershipApplicationResponse>>> getPendingSecretaryReview() {
        log.info("REST request to get applications pending secretary review");
        List<MembershipApplicationResponse> responses = applicationService.getPendingSecretaryReview();
        return ResponseEntity.ok(ApiResponse.success("Pending applications retrieved", responses));
    }

    @GetMapping("/pending/chairperson")
    public ResponseEntity<ApiResponse<List<MembershipApplicationResponse>>> getPendingChairpersonReview() {
        log.info("REST request to get applications pending chairperson review");
        List<MembershipApplicationResponse> responses = applicationService.getPendingChairpersonReview();
        return ResponseEntity.ok(ApiResponse.success("Pending applications retrieved", responses));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countByStatus(@PathVariable String status) {
        log.info("REST request to count applications by status: {}", status);
        Long count = applicationService.countByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Application count retrieved", count));
    }
}
