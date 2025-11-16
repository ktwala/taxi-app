package com.taxiservice.dto.financial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankPaymentRequest {

    @NotNull(message = "Association member ID is required")
    private Long assocMemberId;

    private Long levyPaymentId;

    private Long levyFineId;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    private String branchCode;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Transaction reference is required")
    private String transactionReference;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
}
