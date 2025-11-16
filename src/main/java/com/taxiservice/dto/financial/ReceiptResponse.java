package com.taxiservice.dto.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptResponse {

    private Long receiptId;
    private Long assocMemberId;
    private String memberName;
    private Long levyPaymentId;
    private Long levyFineId;
    private Long bankPaymentId;
    private String receiptNumber;
    private String issuedBy;
    private LocalDateTime issuedDate;
}
