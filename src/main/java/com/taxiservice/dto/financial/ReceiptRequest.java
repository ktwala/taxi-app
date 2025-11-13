package com.taxiservice.dto.financial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptRequest {

    @NotNull(message = "Association member ID is required")
    private Long assocMemberId;

    private Long levyPaymentId;

    private Long levyFineId;

    private Long bankPaymentId;

    @NotBlank(message = "Receipt number is required")
    private String receiptNumber;

    @NotBlank(message = "Issued by is required")
    private String issuedBy;
}
