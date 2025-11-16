package com.taxiservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_payment_id")
    private Long bankPaymentId;

    @NotNull(message = "Association member ID is required")
    @Column(name = "assoc_member_id", nullable = false)
    private Long assocMemberId;

    @Column(name = "levy_payment_id")
    private Long levyPaymentId;

    @Column(name = "levy_fine_id")
    private Long levyFineId;

    @NotBlank(message = "Bank name is required")
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "branch_code", length = 20)
    private String branchCode;

    @NotBlank(message = "Account number is required")
    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @NotBlank(message = "Transaction reference is required")
    @Column(name = "transaction_reference", nullable = false, unique = true, length = 50)
    private String transactionReference;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
