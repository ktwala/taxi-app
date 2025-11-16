package com.taxiservice.dto.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFinanceRequest {

    @NotNull(message = "Association member ID is required")
    private Long assocMemberId;

    @NotNull(message = "Joining fee amount is required")
    @Positive(message = "Joining fee amount must be positive")
    private BigDecimal joiningFeeAmount;

    @Builder.Default
    private Boolean joiningFeePaid = false;

    @Builder.Default
    private Boolean membershipCardIssued = false;
}
