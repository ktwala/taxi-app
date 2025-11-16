package com.taxiservice.repository;

import com.taxiservice.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    List<Receipt> findByAssocMemberId(Long assocMemberId);

    List<Receipt> findByLevyPaymentId(Long levyPaymentId);

    List<Receipt> findByLevyFineId(Long levyFineId);

    List<Receipt> findByBankPaymentId(Long bankPaymentId);

    List<Receipt> findByIssuedBy(String issuedBy);

    @Query("SELECT r FROM Receipt r WHERE r.issuedDate BETWEEN :startDate AND :endDate")
    List<Receipt> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    boolean existsByReceiptNumber(String receiptNumber);
}
