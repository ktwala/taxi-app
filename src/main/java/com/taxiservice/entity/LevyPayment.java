package com.taxiservice.entity;

import com.taxiservice.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "levy_payments")
@EntityListeners({AuditingEntityListener.class, com.taxiservice.audit.AuditEntityListener.class})
@Auditable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevyPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "levy_payment_id")
    private Long levyPaymentId;

    @NotNull(message = "Association member ID is required")
    @Column(name = "assoc_member_id", nullable = false)
    private Long assocMemberId;

    @NotNull(message = "Week start date is required")
    @Column(name = "week_start_date", nullable = false)
    private LocalDate weekStartDate;

    @NotNull(message = "Week end date is required")
    @Column(name = "week_end_date", nullable = false)
    private LocalDate weekEndDate;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatus = "Pending";

    @Column(name = "payment_method_id")
    private Long paymentMethodId;

    @Column(name = "receipt_number", length = 50)
    private String receiptNumber;

    @NotBlank(message = "Created by is required")
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
