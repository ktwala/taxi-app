package com.taxiservice.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplinaryWorkflowResponse {

    private Long workflowId;
    private Long levyFineId;
    private Long assocMemberId;
    private String memberName;
    private String caseStatement;
    private String secretaryDecision;
    private String chairpersonDecision;
    private String paymentArrangement;
    private Boolean chairpersonOverride;
    private String finalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
