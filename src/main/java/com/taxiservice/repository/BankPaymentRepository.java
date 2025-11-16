package com.taxiservice.repository;

import com.taxiservice.entity.BankPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankPaymentRepository extends JpaRepository<BankPayment, Long> {

    Optional<BankPayment> findByTransactionReference(String transactionReference);

    List<BankPayment> findByAssocMemberId(Long assocMemberId);

    List<BankPayment> findByVerified(Boolean verified);

    List<BankPayment> findByLevyPaymentId(Long levyPaymentId);

    List<BankPayment> findByLevyFineId(Long levyFineId);

    @Query("SELECT b FROM BankPayment b WHERE b.verified = false")
    List<BankPayment> findUnverifiedPayments();

    boolean existsByTransactionReference(String transactionReference);
}
