package com.taxiservice.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplinaryWorkflowRequest {

    @NotNull(message = "Levy fine ID is required")
    private Long levyFineId;

    @NotNull(message = "Association member ID is required")
    private Long assocMemberId;

    @NotBlank(message = "Case statement is required")
    private String caseStatement;
}
