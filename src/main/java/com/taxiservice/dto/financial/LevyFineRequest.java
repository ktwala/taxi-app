package com.taxiservice.dto.financial;

import jakarta.validation.constraints.NotBlank;
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
public class LevyFineRequest {

    @NotNull(message = "Association member ID is required")
    private Long assocMemberId;

    @NotNull(message = "Fine amount is required")
    @Positive(message = "Fine amount must be positive")
    private BigDecimal fineAmount;

    @NotBlank(message = "Fine reason is required")
    private String fineReason;

    private String fineStatus = "Unpaid";

    private Long paymentMethodId;
}
