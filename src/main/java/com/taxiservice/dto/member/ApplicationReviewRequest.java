package com.taxiservice.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationReviewRequest {

    @NotBlank(message = "Decision is required")
    private String decision; // Approved, Rejected, Interview

    private String decisionNotes;
}
