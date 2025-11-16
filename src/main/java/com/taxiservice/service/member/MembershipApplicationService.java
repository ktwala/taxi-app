package com.taxiservice.service.member;

import com.taxiservice.dto.member.ApplicationReviewRequest;
import com.taxiservice.dto.member.MembershipApplicationRequest;
import com.taxiservice.dto.member.MembershipApplicationResponse;
import com.taxiservice.entity.MembershipApplication;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.MembershipApplicationRepository;
import com.taxiservice.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipApplicationService {

    private final MembershipApplicationRepository applicationRepository;
    private final RouteRepository routeRepository;

    public MembershipApplicationResponse submitApplication(MembershipApplicationRequest request) {
        log.info("Submitting membership application for: {}", request.getApplicantName());

        // Validate route if provided
        if (request.getRouteId() != null) {
            routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", request.getRouteId()));
        }

        MembershipApplication application = MembershipApplication.builder()
                .applicantName(request.getApplicantName())
                .contactNumber(request.getContactNumber())
                .routeId(request.getRouteId())
                .applicationStatus("Pending")
                .secretaryReviewed(false)
                .chairpersonReviewed(false)
                .build();

        MembershipApplication saved = applicationRepository.save(application);
        log.info("Membership application submitted successfully with ID: {}", saved.getApplicationId());

        return convertToResponse(saved);
    }

    public MembershipApplicationResponse secretaryReview(Long applicationId, ApplicationReviewRequest request) {
        log.info("Secretary reviewing application ID: {} with decision: {}", applicationId, request.getDecision());

        MembershipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipApplication", "applicationId", applicationId));

        if (application.getSecretaryReviewed()) {
            throw new IllegalStateException("Application has already been reviewed by secretary");
        }

        application.setSecretaryReviewed(true);
        application.setApplicationStatus(request.getDecision());
        application.setDecisionNotes(request.getDecisionNotes());

        MembershipApplication updated = applicationRepository.save(application);
        log.info("Secretary review completed for application ID: {}", applicationId);

        return convertToResponse(updated);
    }

    public MembershipApplicationResponse chairpersonReview(Long applicationId, ApplicationReviewRequest request) {
        log.info("Chairperson reviewing application ID: {} with decision: {}", applicationId, request.getDecision());

        MembershipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipApplication", "applicationId", applicationId));

        if (!application.getSecretaryReviewed()) {
            throw new IllegalStateException("Application must be reviewed by secretary first");
        }

        if (application.getChairpersonReviewed()) {
            throw new IllegalStateException("Application has already been reviewed by chairperson");
        }

        application.setChairpersonReviewed(true);
        application.setApplicationStatus(request.getDecision());
        if (request.getDecisionNotes() != null) {
            String notes = application.getDecisionNotes() != null ?
                    application.getDecisionNotes() + "\nChairperson: " + request.getDecisionNotes() :
                    "Chairperson: " + request.getDecisionNotes();
            application.setDecisionNotes(notes);
        }

        MembershipApplication updated = applicationRepository.save(application);
        log.info("Chairperson review completed for application ID: {}", applicationId);

        return convertToResponse(updated);
    }

    public MembershipApplicationResponse updateApplicationStatus(Long applicationId, String status) {
        log.info("Updating application ID: {} to status: {}", applicationId, status);

        MembershipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipApplication", "applicationId", applicationId));

        application.setApplicationStatus(status);

        MembershipApplication updated = applicationRepository.save(application);
        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public MembershipApplicationResponse getApplicationById(Long applicationId) {
        log.info("Fetching membership application with ID: {}", applicationId);

        MembershipApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("MembershipApplication", "applicationId", applicationId));

        return convertToResponse(application);
    }

    @Transactional(readOnly = true)
    public List<MembershipApplicationResponse> getAllApplications() {
        log.info("Fetching all membership applications");

        return applicationRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembershipApplicationResponse> getApplicationsByStatus(String status) {
        log.info("Fetching applications with status: {}", status);

        return applicationRepository.findByApplicationStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembershipApplicationResponse> getPendingSecretaryReview() {
        log.info("Fetching applications pending secretary review");

        return applicationRepository.findPendingSecretaryReview().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembershipApplicationResponse> getPendingChairpersonReview() {
        log.info("Fetching applications pending chairperson review");

        return applicationRepository.findPendingChairpersonReview().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countByStatus(String status) {
        return applicationRepository.countByStatus(status);
    }

    private MembershipApplicationResponse convertToResponse(MembershipApplication application) {
        MembershipApplicationResponse response = MembershipApplicationResponse.builder()
                .applicationId(application.getApplicationId())
                .applicantName(application.getApplicantName())
                .contactNumber(application.getContactNumber())
                .applicationStatus(application.getApplicationStatus())
                .routeId(application.getRouteId())
                .secretaryReviewed(application.getSecretaryReviewed())
                .chairpersonReviewed(application.getChairpersonReviewed())
                .decisionNotes(application.getDecisionNotes())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();

        // Fetch route name if available
        if (application.getRouteId() != null) {
            routeRepository.findById(application.getRouteId())
                    .ifPresent(route -> response.setRouteName(route.getName()));
        }

        return response;
    }
}
