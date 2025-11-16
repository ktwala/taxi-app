package com.taxiservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "levy_fine_disciplinary_workflow")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevyFineDisciplinaryWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workflow_id")
    private Long workflowId;

    @NotNull(message = "Levy fine ID is required")
    @Column(name = "levy_fine_id", nullable = false)
    private Long levyFineId;

    @NotNull(message = "Association member ID is required")
    @Column(name = "assoc_member_id", nullable = false)
    private Long assocMemberId;

    @NotBlank(message = "Case statement is required")
    @Column(name = "case_statement", nullable = false, columnDefinition = "TEXT")
    private String caseStatement;

    @Column(name = "secretary_decision", nullable = false, length = 50)
    private String secretaryDecision = "Pending";

    @Column(name = "chairperson_decision", nullable = false, length = 50)
    private String chairpersonDecision = "Pending";

    @Column(name = "payment_arrangement", columnDefinition = "TEXT")
    private String paymentArrangement;

    @Column(name = "chairperson_override", nullable = false)
    private Boolean chairpersonOverride = false;

    @Column(name = "final_status", nullable = false, length = 50)
    private String finalStatus = "Ongoing";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
