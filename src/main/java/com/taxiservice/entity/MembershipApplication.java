package com.taxiservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "membership_application")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @NotBlank(message = "Applicant name is required")
    @Column(name = "applicant_name", nullable = false, length = 100)
    private String applicantName;

    @NotBlank(message = "Contact number is required")
    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "application_status", nullable = false, length = 50)
    private String applicationStatus = "Pending";

    @Column(name = "route_id")
    private Long routeId;

    @Column(name = "secretary_reviewed", nullable = false)
    private Boolean secretaryReviewed = false;

    @Column(name = "chairperson_reviewed", nullable = false)
    private Boolean chairpersonReviewed = false;

    @Column(name = "decision_notes", columnDefinition = "TEXT")
    private String decisionNotes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
