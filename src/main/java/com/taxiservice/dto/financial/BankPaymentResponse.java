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
public class BankPaymentResponse {

    private Long bankPaymentId;
    private Long assocMemberId;
    private String memberName;
    private Long levyPaymentId;
    private Long levyFineId;
    private String bankName;
    private String branchCode;
    private String accountNumber;
    private String transactionReference;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private Boolean verified;
    private LocalDateTime createdAt;
}
