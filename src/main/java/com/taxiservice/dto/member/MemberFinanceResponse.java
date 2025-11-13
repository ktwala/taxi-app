package com.taxiservice.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFinanceResponse {

    private Long financeId;
    private Long assocMemberId;
    private String memberName;
    private Boolean joiningFeePaid;
    private BigDecimal joiningFeeAmount;
    private Boolean membershipCardIssued;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
