package com.taxiservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_finance")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFinance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "finance_id")
    private Long financeId;

    @NotNull(message = "Association member ID is required")
    @Column(name = "assoc_member_id", nullable = false)
    private Long assocMemberId;

    @Column(name = "joining_fee_paid", nullable = false)
    private Boolean joiningFeePaid = false;

    @NotNull(message = "Joining fee amount is required")
    @Column(name = "joining_fee_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal joiningFeeAmount;

    @Column(name = "membership_card_issued", nullable = false)
    private Boolean membershipCardIssued = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
