package com.taxiservice.repository;

import com.taxiservice.entity.LevyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LevyPaymentRepository extends JpaRepository<LevyPayment, Long> {

    List<LevyPayment> findByAssocMemberId(Long assocMemberId);

    List<LevyPayment> findByPaymentStatus(String status);

    List<LevyPayment> findByAssocMemberIdAndPaymentStatus(Long assocMemberId, String status);

    @Query("SELECT l FROM LevyPayment l WHERE l.weekStartDate >= :startDate AND l.weekEndDate <= :endDate")
    List<LevyPayment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM LevyPayment l WHERE l.paymentStatus = 'Pending'")
    List<LevyPayment> findPendingPayments();

    @Query("SELECT SUM(l.amount) FROM LevyPayment l WHERE l.assocMemberId = :memberId AND l.paymentStatus = 'Paid'")
    BigDecimal getTotalPaidByMember(@Param("memberId") Long memberId);

    @Query("SELECT SUM(l.amount) FROM LevyPayment l WHERE l.paymentStatus = 'Paid' AND l.weekStartDate >= :startDate AND l.weekEndDate <= :endDate")
    BigDecimal getTotalCollectedInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(l) FROM LevyPayment l WHERE l.assocMemberId = :memberId AND l.paymentStatus = 'Pending'")
    Long countPendingByMember(@Param("memberId") Long memberId);
}
