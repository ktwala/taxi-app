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
public class AssocMemberResponse {

    private Long assocMemberId;
    private String name;
    private String contactNumber;
    private String squadNumber;
    private LocalDateTime joinedAt;
    private Boolean blacklisted;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
