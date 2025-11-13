package com.taxiservice.dto.financial;

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
public class LevyFineResponse {

    private Long levyFineId;
    private Long assocMemberId;
    private String memberName;
    private String squadNumber;
    private BigDecimal fineAmount;
    private String fineReason;
    private String fineStatus;
    private Long paymentMethodId;
    private String paymentMethodName;
    private String receiptNumber;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
