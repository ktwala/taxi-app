package com.taxiservice.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipApplicationResponse {

    private Long applicationId;
    private String applicantName;
    private String contactNumber;
    private String applicationStatus;
    private Long routeId;
    private String routeName;
    private Boolean secretaryReviewed;
    private Boolean chairpersonReviewed;
    private String decisionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
