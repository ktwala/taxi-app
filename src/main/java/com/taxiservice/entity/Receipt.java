package com.taxiservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @NotNull(message = "Association member ID is required")
    @Column(name = "assoc_member_id", nullable = false)
    private Long assocMemberId;

    @Column(name = "levy_payment_id")
    private Long levyPaymentId;

    @Column(name = "levy_fine_id")
    private Long levyFineId;

    @Column(name = "bank_payment_id")
    private Long bankPaymentId;

    @NotBlank(message = "Receipt number is required")
    @Column(name = "receipt_number", nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @NotBlank(message = "Issued by is required")
    @Column(name = "issued_by", nullable = false, length = 100)
    private String issuedBy;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate = LocalDateTime.now();
}
