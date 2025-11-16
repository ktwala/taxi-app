package com.taxiservice.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowDecisionRequest {

    @NotBlank(message = "Decision is required")
    private String decision; // Approved, Rejected

    private String paymentArrangement;

    @Builder.Default
    private Boolean override = false;
}
