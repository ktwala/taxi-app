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
public class MembershipApplicationRequest {

    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    private Long routeId;

    private String applicationStatus = "Pending";
}
