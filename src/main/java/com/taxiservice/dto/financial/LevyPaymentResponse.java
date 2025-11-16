package com.taxiservice.dto.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevyPaymentResponse {

    private Long levyPaymentId;
    private Long assocMemberId;
    private String memberName;
    private String squadNumber;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private BigDecimal amount;
    private String paymentStatus;
    private Long paymentMethodId;
    private String paymentMethodName;
    private String receiptNumber;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
